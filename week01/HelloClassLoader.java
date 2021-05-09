package jvm;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;


public class HelloClassLoader extends ClassLoader{

    public static void main(String[] args){
        try {
            String className = "Hello";
            String methodName = "hello";

            HelloClassLoader classloader = new HelloClassLoader();
            Class<?> clazz = classloader.loadClass(className);
            for (Method m :clazz.getDeclaredMethods()){
                System.out.println(clazz.getSimpleName() + "." + m.getName());
            }
            Object instance = clazz.getConstructor().newInstance();
            Method method = clazz.getMethod(methodName);
            method.invoke(instance);

        }
        catch (Exception ex){
            ex.printStackTrace();
        }
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        String helloPath = "src/main/resources/Hello.xlass";
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(helloPath);
            int length = inputStream.available();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);

            for (int i = 0;i < bytes.length;i++){
                bytes[i] = (byte)(255 - bytes[i]);
            }
            return defineClass(name,bytes,0,bytes.length);
        } catch (IOException e) {
            e.printStackTrace();
            throw new ClassNotFoundException(name,e);
        }
        finally {
            try {
                if (inputStream != null){
                    inputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
