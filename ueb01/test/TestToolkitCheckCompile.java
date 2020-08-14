import org.junit.Assert;

/**
 * Toolkit, das einen Java-Compiler kapselt. Stellt die Methoden 
 * checkForError(String className) und checkForSuccess(String className) zur
 * Verfügung, in denen mittels JUnit überprüft werden kann, ob die übergebene
 * Datei nicht kompiliert bzw. ob sie dies tut.
 * 
 * Die Namen der zu überprüfenden Klassen sollten die Texte "DoCompile" bzw.
 * "DontCompile" enthalten, damit diese nicht im ant-Aufruf kompiliert werden.
 * 
 * @author kar, mhe
 */
public class TestToolkitCheckCompile {

    static final int TIMEOUT = 1000 * 5;

    static final String DIR = "./";

    static final String pathSep = System.getProperty("path.separator");

    protected TestToolkitShellWrapper executeProcess(String className) {
        TestToolkitShellWrapper shell =
                new TestToolkitShellWrapper(DIR, new String[] { "javac", "-cp",
                        "bin", "test/" + className + ".java" }, TIMEOUT);

        shell.execute();
        return shell;
    }

    private void printSdtOutErr(TestToolkitShellWrapper shell){
        if (!shell.getStdOut().equals("")) {
            System.out.println("stdout: " + shell.getStdOut());
        }
        if (!shell.getStdErr().equals("")) {
            System.out.println("stderr: " + shell.getStdErr());
        }
    }

    /**
     * Überprüft mit JUnit, ob eine bestimmte Java-Klasse nicht kompiliert.
     * 
     * @param className Der Name der zu überprüfenden Java-Klasse
     */
    public void checkForError(String className) {
        TestToolkitShellWrapper shell = executeProcess(className);
        final int returnValue = shell.getReturnValue();

        System.out.println("compiling " + className);
        this.printSdtOutErr(shell);
        System.out.println("");
        Assert.assertTrue(shell.getStdErr().contains("1 error"));
        Assert.assertEquals("Unexpected return code: " + returnValue, 1,
                returnValue);
    }

    /**
     * Überprüft mit JUnit, ob eine bestimmte Java-Klasse kompiliert.
     * 
     * @param className Der Name der zu überprüfenden Java-Klasse
     */
    public void checkForSuccess(String className) {
        TestToolkitShellWrapper shell = executeProcess(className);
        final int returnValue = shell.getReturnValue();

        System.out.println("compiling " + className);
        this.printSdtOutErr(shell);
        System.out.println("");
        Assert.assertEquals("Unexpected return code: " + returnValue, 0,
                returnValue);
    }

}
