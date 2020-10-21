package cn.study.jk.week01;

import java.io.*;
import java.lang.reflect.InvocationTargetException;

public class CccJvm extends ClassLoader {
	
    private String path;

    public CccJvm(String path) {
        this.path = path;
    }

    public CccJvm(ClassLoader parent, String path) {
        super(parent);
        this.path = path;
    }

    public static void main(String[] args) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        CccJvm cccJvm = new CccJvm("C:\\Users\\jianjun.chai\\JAVA-000\\Week_01\\");
        try {
            Class<?> hello = cccJvm.loadClass("Hello");
            Object o = hello.newInstance();

            hello.getMethod("hello").invoke(o);

        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }


    }

    @Override
    protected Class<?> findClass(String name)  {
        String fileName = path + name + ".xlass";
        BufferedInputStream bis = null;
        ByteArrayOutputStream baos = null;
        try {
            bis = new BufferedInputStream(new FileInputStream(fileName));
            baos = new ByteArrayOutputStream();
            int len;
            byte[] data = new byte[1024];
            while ((len = bis.read(data)) != -1) {
                baos.write(data,0,len);
            }
            byte[] bytes = baos.toByteArray();
            for (int i = 0; i < bytes.length; i++) {
                bytes[i] = (byte) (255 - bytes[i]);
            }
            Class clazz = defineClass(null,bytes,0,bytes.length);
            return clazz;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (bis != null) {
                    bis.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                if (baos != null) {
                    bis.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        return null;
    }
}
