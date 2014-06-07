package com.blizzardtec;

import org.apache.bcel.Constants;
import org.apache.bcel.generic.ArrayType;
import org.apache.bcel.generic.ClassGen;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.InstructionConstants;
import org.apache.bcel.generic.InstructionFactory;
import org.apache.bcel.generic.InstructionList;
import org.apache.bcel.generic.MethodGen;
import org.apache.bcel.generic.ObjectType;
import org.apache.bcel.generic.PUSH;
import org.apache.bcel.generic.Type;

/**
 * blah.
 * @author Barnaby Golden
 *
 */
@SuppressWarnings("PMD.UseSingleton")
public final class MakeClazz {

    /**
     * Private.
     */
    private MakeClazz() {
        super();
    }

    /**
     * blah.
     * @param args param
     * @throws Exception thrown
     */
    public static void main(final String[] args) throws Exception {

        // public HellowWorld extends java.lang.Object
        ClassGen classGen = new ClassGen("HelloWorld", "java.lang.Object", "",
                Constants.ACC_PUBLIC | Constants.ACC_SUPER, null);

        ConstantPoolGen constantPoolGen = classGen.getConstantPool();
        InstructionList instructionList = new InstructionList();

        final InstructionFactory instructionFactory =
                                new InstructionFactory(classGen);

        classGen.addEmptyConstructor(Constants.ACC_PUBLIC);

        // System.out.println
        ObjectType printStream = new ObjectType("java.io.PrintStream");

        instructionList.append(instructionFactory.createFieldAccess(
                "java.lang.System", "out", printStream, Constants.GETSTATIC));

        // "Hello world!!"
        instructionList.append(new PUSH(constantPoolGen, "Hello World!!"));

        instructionList.append(instructionFactory.createInvoke(
                "java.io.PrintStream", "println", Type.VOID,
                new Type[] {Type.STRING}, Constants.INVOKEVIRTUAL));

        instructionList.append(InstructionConstants.RETURN);

        // public static void main
        MethodGen methodGen = new MethodGen(Constants.ACC_PUBLIC
                | Constants.ACC_STATIC,
                Type.VOID,

                // (String[] args)
                new Type[] {new ArrayType(Type.STRING, 1)},
                new String[] {"args"}, "main", "HelloWorld", instructionList,
                constantPoolGen);

        //instructionList.setPositions();
        methodGen.setMaxStack();
        //methodGen.setMaxLocals();
        //methodGen.removeLineNumbers();

        classGen.addMethod(methodGen.getMethod());

        instructionList.dispose();

        classGen.getJavaClass().dump("HelloWorld.class");
    }
}
