/**
 * 
 */
package com.blizzardtec;

import java.io.IOException;

import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.generic.ClassGen;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.InstructionFactory;
import org.apache.bcel.generic.InstructionList;
import org.apache.bcel.generic.MethodGen;
import org.apache.bcel.generic.FieldGen;
import org.apache.bcel.generic.Type;
import org.apache.bcel.Constants;
import org.apache.bcel.Repository;

/**
 * @author Barnaby Golden
 * 
 */
public final class ModClazz {

    private ClassGen myClassGen;
    private ConstantPoolGen cp;
    private InstructionFactory factory;

    /**
     * @param args
     */
    public static void main(String[] args) {

        final ModClazz modClazz = new ModClazz();
        modClazz.buildClass("Vehicle", "com.blizzardtec", new String[] {"mileage"});

        System.out.println("Class generated");
    }

    public void buildClass(final String className, final String packageName, final String[] fields) {

        final String qualifiedClassName = packageName + "." + className;

        JavaClass myClass = null;

        try {
            myClass = Repository.lookupClass(qualifiedClassName);
        } catch (ClassNotFoundException cfe) {
            System.out.println(cfe.getMessage());
            System.exit(1);
        }

        myClassGen = new ClassGen(myClass);

        factory = new InstructionFactory(myClassGen);
        cp = myClassGen.getConstantPool();

        // for each input string, add a private field, setter and getter
        for (int i = 0; i < fields.length; i++) {
            FieldGen fg =
                new FieldGen(Constants.ACC_PRIVATE, Type.INT, fields[i], cp);

            myClassGen.addField(fg.getField());

            createGetter(qualifiedClassName, fields[i]);
            createSetter(qualifiedClassName, fields[i]);            
        }

        // write out the new class file
        try {
            myClassGen.getJavaClass().dump(className + ".class");
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
            System.exit(1);
        }        
    }

    /**
     * Create a getter method.
     * @param qualifiedClassName full package and class name
     * @param field the field the getter is for
     */
    private void createGetter(final String qualifiedClassName, final String field) {

        final InstructionList il = new InstructionList();

        il.append(InstructionFactory.createLoad(Type.OBJECT, 0));
        il.append(factory.createFieldAccess(qualifiedClassName, field, Type.INT, Constants.GETFIELD));
        il.append(InstructionFactory.createReturn(Type.INT));

        MethodGen methodGen = new MethodGen(Constants.ACC_PUBLIC, Type.INT,
                Type.NO_ARGS, new String[] {}, fieldToGetter(field), qualifiedClassName, il,
                cp);

        methodGen.setMaxStack();
        methodGen.setMaxLocals();

        myClassGen.addMethod(methodGen.getMethod());
        il.dispose();        
    }

    /**
     * Create a setter method.
     * @param qualifiedClassName full package and class name
     * @param field the field the setter is for
     */
    private void createSetter(final String qualifiedClassName, final String field) {

        final InstructionList il = new InstructionList();

        il.append(InstructionFactory.createLoad(Type.OBJECT, 0));
        il.append(InstructionFactory.createLoad(Type.INT, 1));
        il.append(factory.createFieldAccess(qualifiedClassName, field, Type.INT, Constants.PUTFIELD));
        il.append(InstructionFactory.createReturn(Type.VOID));

        MethodGen methodGen = new MethodGen(Constants.ACC_PUBLIC, Type.VOID,
                new Type[] { Type.INT }, new String[] { "arg0" }, fieldToSetter(field),
                qualifiedClassName, il, cp);

        methodGen.setMaxStack();
        methodGen.setMaxLocals();

        myClassGen.addMethod(methodGen.getMethod());
        il.dispose();
    }

    /**
     * Turn a field name in to its equivalent setter method name.
     * @param field field name
     * @return setter method name
     */
    private String fieldToSetter(final String field) {

        String firstLetterUpper = field.substring(0,1).toUpperCase() + field.substring(1);

        return "set" + firstLetterUpper;
    }

    private String fieldToGetter(final String field) {

        String firstLetterUpper = field.substring(0,1).toUpperCase() + field.substring(1);

        return "get" + firstLetterUpper;
    }
}
