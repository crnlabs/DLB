package dontlookback;

public class DontLookBack {

    public static void main(String[] args) {
        String modelPath = System.getProperty("user.dir") + "/resources/models/";
        System.out.println("Don't look back.");
        System.out.println("A Game By:");
        System.out.println("Carl & James");
        System.out.println("~*~*~*~*~*~*~*~*~*~*~*~*~**~*~");
        System.out.println(modelPath + "monkey3.obj");
        System.out.println("The following is movement tracking:");
        
        DLB_Graphics Graphics = new DLB_Graphics();
        
    }
}
