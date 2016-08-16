/*
 * $Log: DiffTest.java,v $
 * Revision 1.7  2013/04/01 16:27:31  stuart
 * Fix DiffPrint unified format with test cases.
 * Begin porting some diff-2.7 features.
 *
 * Revision 1.6  2013/02/26 21:07:08  stuart
 * Release 1.4
 *
 * Revision 1.5  2010/03/03 21:21:25  stuart
 * Test new direct equivalence API
 *
 * Revision 1.4  2007/12/20 05:04:21  stuart
 * Can no longer import from default package.
 *
 * Revision 1.3  2006/09/28 17:10:41  stuart
 * New Diff test case.  DateDelta.
 *
 * Revision 1.2  2004/01/29 02:35:35  stuart
 * Test for out of bounds exception in UnifiedPrint.print_hunk.
 * Add setOutput() to DiffPrint.Base.
 *
 * Revision 1.1  2003/05/29 20:22:40  stuart
 * Test EditMask.getFix()
 *
 */
package bmsi.util;

import static org.testng.Assert.*;

import org.testng.annotations.*;

import java.io.StringWriter;

/** Test Diff behavior.
  @author Stuart D. Gathman
  Copyright (C) 2002 Business Management Systems, Inc.
 */
public class DiffTest {

  private static String[] f1 = { "hello" };
  private static String[] f2 = { "hello", "bye" };

  @Test
  public void testReverse() {
    Diff diff = new Diff(f1,f2);
    Diff.change script = diff.diff_2(true);
    assertTrue(script != null);
    assertTrue(script.link == null);
  }

  /** For Java versions without auto-boxing. */
  private Integer[] loadArray(int[] a) {
    Integer[] b = new Integer[a.length];
    for (int i = 0; i < a.length; ++i)
      b[i] = new Integer(a[i]);
    return b;
  }

  /** This was causing an array out of bounds exception.
    Submitted by Markus Oellinger. */
  @Test
  public void testSwap() {
    final Integer[] l1 = loadArray(new int[] { 1,2,4,7,9,35,56,58,76 });
    final Integer[] l2 = loadArray(new int[] { 1,2,4,76,9,35,56,58,7 });
    Diff diff = new Diff(l1,l2);
    Diff.change script = diff.diff_2(false);
    // script should have two changes
    assertTrue(script != null);
    assertTrue(script.link != null);
    assertTrue(script.link.link == null);
    assertEquals(1,script.inserted);
    assertEquals(1,script.deleted);
    assertEquals(3,script.line0);
    assertEquals(3,script.line1);
    assertEquals(1,script.link.inserted);
    assertEquals(1,script.link.deleted);
    assertEquals(8,script.link.line0);
    assertEquals(8,script.link.line1);
    DiffPrint.Base p = new DiffPrint.UnifiedPrint(l1,l2);
    StringWriter sw = new StringWriter();
    p.setOutput(sw);
    p.print_script(script);
    assertEquals("@@ -1,9 +1,9 @@\n 1\n 2\n 4\n-7\n+76\n 9\n 35\n 56\n"+
	" 58\n-76\n+7\n", sw.toString()
    );
  }

  private int[] loadChar(String s) {
    int[] a = new int[s.length()];
    for (int i = 0; i < a.length; ++i) a[i] = s.charAt(i);
    return a;
  }

  /** Test passing equivalence arrays. */
  @Test
  public void testInt() {
    final int[] l1 = loadChar("abcdef");
    final int[] l2 = loadChar("def");
    Diff diff = new Diff(l1,l2);
    Diff.change script = diff.diff(Diff.forwardScript);
    assertTrue(script != null);
  }

  private String[] toStringArray(String s) {
    String[] a = new String[s.length()];
    for (int i = 0; i < a.length; ++i)
      a[i] = String.valueOf(s.charAt(i));
    return a;
  }

  @Test
  private void diffChars(String s1,String s2) {
    final int[] l1 = loadChar(s1);
    final int[] l2 = loadChar(s2);
    Diff diff = new Diff(l1,l2);
    Diff.change script = diff.diff(Diff.forwardScript);
    DiffPrint.Base p = new DiffPrint.UnifiedPrint(
    	toStringArray(s1),toStringArray(s2)
    );
    p.print_script(script);
  }

  private static String[] test1 = {
    "aaa","bbb","ccc","ddd","eee","fff","ggg","hhh","iii"
  };
  private static String[] test2 = {
    "aaa","jjj","kkk","lll","bbb","ccc","hhh","iii","mmm","nnn","ppp"
  };

  /** Test context based output.  Changes past the end of old file
    were causing an array out of bounds exception.
    Submitted by Cristian-Augustin Saita and Adam Rabung.
   */
  @Test
  public void testContext() {
    Diff diff = new Diff(test1,test2);
    Diff.change script = diff.diff_2(false);
    DiffPrint.Base p = new DiffPrint.UnifiedPrint(test1,test2);
    StringWriter wtr = new StringWriter();
    p.setOutput(wtr);
    //p.print_header("test1","test2");
    p.print_script(script);
    /* FIXME: when DiffPrint is updated to diff-2.7, testfor expected
       output in wtr.toString(). diff-1.15 does not combine adjacent
       changes when they are close together. */
  }

  /** UnifiedPrint crasher.
   * Submitted by Dennis Hendriks
   */
  @Test
  public void testUnifiedPrint() {
      String data1[] = {"import \"import3.seal\";",
			"",
			"const bool c2 = true;"};

      String data2[] = {"const bool c2 = true;",
			"group import3:",
			"  const bool c3 = true;",
			"end"};
      String expected = "--- data1\n+++ data2\n"+
"@@ -1,3 +1,4 @@\n"+
"-import \"import3.seal\";\n"+
"-\n"+
" const bool c2 = true;\n"+
"+group import3:\n"+
"+  const bool c3 = true;\n"+
"+end\n";

      Diff d = new Diff(data1, data2);
      Diff.change rslt = d.diff_2(false);
      DiffPrint.UnifiedPrint dp = new DiffPrint.UnifiedPrint(data1, data2);
      StringWriter sw = new StringWriter();
      dp.setOutput(sw);
      dp.print_header("data1", "data2");
      dp.print_script(rslt);
      assertEquals(expected,sw.toString());
  } 

  /** Submitted by Kristian Eide <kreide@gmail.com> */
  @Test
  public void testUnified() {
        String[] expected = new String[] { "a", "", "a" };
        String[] actual = new String[] { "", "" };
	String res = "@@ -1,3 +1,2 @@\n-a\n \n-a\n+\n";

        Diff.change c = new Diff(expected, actual).diff_2(false);

        DiffPrint.UnifiedPrint p = new DiffPrint.UnifiedPrint(expected, actual);
        StringWriter sw = new StringWriter();
        p.setOutput(sw);
        p.print_script(c);
	diffChars(res,sw.toString());
	assertEquals(res,sw.toString());
  }
}
