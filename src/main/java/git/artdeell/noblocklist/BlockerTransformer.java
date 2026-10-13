package git.artdeell.noblocklist;

import org.objectweb.asm.*;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.Random;

public class BlockerTransformer implements ClassFileTransformer {
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        if (!"java/net/URL".equals(className)) return null;
        try {
            ClassReader cr = new ClassReader(classfileBuffer);
            ClassWriter cw = new ClassWriter(cr, ClassWriter.COMPUTE_FRAMES);
            URLVisitor cv = new URLVisitor(cw);
            cr.accept(cv, 0);
            System.out.println("[NoBlocklist] Now, the blocked server list should not be available");
            return cw.toByteArray();
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return null;
    }

    private static class URLVisitor extends ClassVisitor {
        public URLVisitor(ClassVisitor classVisitor) {
            super(Opcodes.ASM9, classVisitor);
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
            MethodVisitor methodVisitor = super.visitMethod(access, name, descriptor, signature, exceptions);

            if(!"openConnection".equals(name)) return methodVisitor;

            if(
                    !"(Ljava/net/Proxy;)Ljava/net/URLConnection;".equals(descriptor) &&
                    !"()Ljava/net/URLConnection;".equals(descriptor)
            ) return methodVisitor;

            return new URLMethodVisitor(methodVisitor);
        }
    }

    private static class URLMethodVisitor extends MethodVisitor {
        private final int plausibleLineNumber;
        public URLMethodVisitor(MethodVisitor mv) {
            super(Opcodes.ASM9, mv);
            plausibleLineNumber = new Random(System.nanoTime()).nextInt(1000,1100);
        }

        private void urlStringCondition(String method, String expectedValue, Label onNeqLabel) {
            mv.visitVarInsn(Opcodes.ALOAD, 0);
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/net/URL", method, "()Ljava/lang/String;", false);

            mv.visitLdcInsn(expectedValue);

            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/String", "equalsIgnoreCase", "(Ljava/lang/String;)Z", false);

            mv.visitJumpInsn(Opcodes.IFEQ, onNeqLabel);
        }

        @Override
        public void visitCode() {
            Label allowedLabel = new Label();
            Label fakeLineMarker = new Label();

            urlStringCondition("getHost", "sessionserver.mojang.com", allowedLabel);
            urlStringCondition("getPath", "/blockedservers", allowedLabel);

            mv.visitLineNumber(plausibleLineNumber, fakeLineMarker);
            mv.visitLabel(fakeLineMarker);

            /*mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
            mv.visitLdcInsn("[NoBlocklist] Someone's being sneaky-sneaky");
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);*/

            mv.visitTypeInsn(Opcodes.NEW, "java/io/IOException");
            mv.visitInsn(Opcodes.DUP);
            mv.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/io/IOException", "<init>", "()V", false);
            mv.visitInsn(Opcodes.ATHROW);

            mv.visitLabel(allowedLabel);
            super.visitCode();
        }
    }
}
