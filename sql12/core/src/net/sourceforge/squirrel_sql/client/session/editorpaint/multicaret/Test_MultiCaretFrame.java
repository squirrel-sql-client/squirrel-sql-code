package net.sourceforge.squirrel_sql.client.session.editorpaint.multicaret;

import net.sourceforge.squirrel_sql.client.session.editorpaint.EditorPaintService;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import java.awt.GridLayout;

public class Test_MultiCaretFrame
{

   private static JFrame frame;
   private static JScrollPane scrollPane;
   private static Test_MultiCaretTextArea textArea;

   public static void main(String[] args)
   {
      SwingUtilities.invokeLater(() -> start());
   }

   private static void start()
   {
      frame = new JFrame();

      frame.getContentPane().setLayout(new GridLayout(1,1));

      textArea = new Test_MultiCaretTextArea();
      textArea.setText(getText());
      textArea.setCaretPosition(0);

      final MultiCaretHandler multiCaretHandler = new MultiCaretHandler(textArea, EditorPaintService.EMPTY);
      textArea.setMultiCaretHandler(multiCaretHandler);


      scrollPane = new JScrollPane(textArea);
      frame.getContentPane().add(scrollPane);

      frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
      frame.setBounds(200,200, 500,500);
      frame.setVisible(true);
   }

   private static String getText()
   {
      final String base = "abc mmm\n" +
                       "def\n" +
                       "abc\n" +
                       "ghi\n" +
                       "abc abc abc\n" +
                       "def\n" +
                       "abc";


      StringBuilder ret = new StringBuilder(base);

      for (int i = 0; i < 20; i++)
      {
         ret.append("\n").append(base);
      }


      return ret.toString();
   }

}
