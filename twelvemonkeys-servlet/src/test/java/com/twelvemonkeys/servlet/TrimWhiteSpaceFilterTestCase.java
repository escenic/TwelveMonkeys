package com.twelvemonkeys.servlet;

import com.twelvemonkeys.io.OutputStreamAbstractTestCase;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.Filter;
import javax.servlet.ServletResponse;

/**
 * TrimWhiteSpaceFilterTestCase
 * <p/>
 *
 * @author <a href="mailto:harald.kuhr@gmail.com">Harald Kuhr</a>
 * @author last modified by $Author: haku $
 * @version $Id: //depot/branches/personal/haraldk/twelvemonkeys/release-2/twelvemonkeys-servlet/src/test/java/com/twelvemonkeys/servlet/TrimWhiteSpaceFilterTestCase.java#1 $
 */
public class TrimWhiteSpaceFilterTestCase extends FilterAbstractTestCase {
    protected Filter makeFilter() {
        return new TrimWhiteSpaceFilter();
    }

    public static final class TrimWSFilterOutputStreamTestCase extends OutputStreamAbstractTestCase {

        protected OutputStream makeObject() {
            // NOTE: ByteArrayOutputStream does not implement flush or close...
            return makeOutputStream(new ByteArrayOutputStream(16));
        }

        protected OutputStream makeOutputStream(OutputStream pWrapped) {
            return new TrimWhiteSpaceFilter.TrimWSFilterOutputStream(pWrapped);
        }

        public void testTrimWSOnlyWS() throws IOException {
            ByteArrayOutputStream out = new ByteArrayOutputStream(64);
            OutputStream trim = makeOutputStream(out);

            String input = "  \n\n\t  \t" + (char) 0x0a + ' ' + (char) 0x0d + "\r ";

            trim.write(input.getBytes());
            trim.flush();
            trim.close();

            assertEquals("Should be trimmed", "\"\"", '"' + new String(out.toByteArray()) + '"');
        }

        public void testTrimWSLeading() throws IOException {
            ByteArrayOutputStream out = new ByteArrayOutputStream(64);
            OutputStream trim = makeOutputStream(out);

            byte[] input = "  \n<?xml version=\"1.0\"?>\n\t  <not-really-well-formed/>   \t".getBytes();
            String trimmed = "<?xml version=\"1.0\"?>\n<not-really-well-formed/> "; // TODO: This is pr spec (the trailing space). But probably quite stupid...

            trim.write(input);
            trim.flush();
            trim.close();

            assertEquals("Should be trimmed", '"' + trimmed + '"', '"' + new String(out.toByteArray()) + '"');
        }

        public void testTrimWSOffsetLength() throws IOException {
            ByteArrayOutputStream out = new ByteArrayOutputStream(64);
            OutputStream trim = makeOutputStream(out);

            // Kindly generated by http://lipsum.org/ :-)
            byte[] input = ("  \n\tLorem ipsum dolor    sit amet, consectetuer   adipiscing elit.\n\r\n\r" +
                    "Etiam arcu neque, \n\rmalesuada blandit,\t\n\r\n\r\n\n\n\r\n\r\r\n\n\t rutrum quis, molestie at, diam.\n" +
                    "                     Nulla elementum elementum eros.\n                    \t\t\n\r" +
                    "Ut rhoncus, turpis in pellentesque volutpat, sapien sem accumsan augue, a scelerisque nibh erat vel magna.\n" +
                    "               Phasellus diam orci, dignissim et, gravida vitae, venenatis eu, elit.\n" +
                    "\t\t\tSuspendisse dictum enim at nisl. Integer magna erat, viverra sit amet, consectetuer nec, accumsan ut, mi.\n" +
                    "\n\r\r\r\n\rNunc ultricies       \n\n\n      consectetuer mauris.                                                       " +
                    "Nulla lectus mauris, viverra ac, pulvinar a, commodo quis, nulla.\n " +
                    "Ut eget nulla. In est dolor, convallis \t non, tincidunt \tvestibulum, porttitor et, eros.\n " +
                    "\t\t \t \n\rDonec vehicula ultrices nisl.").getBytes();

            String trimmed = "Lorem ipsum dolor sit amet, consectetuer adipiscing elit.\n" +
                    "Etiam arcu neque, malesuada blandit,\trutrum quis, molestie at, diam.\n" +
                    "Nulla elementum elementum eros.\n" +
                    "Ut rhoncus, turpis in pellentesque volutpat, sapien sem accumsan augue, a scelerisque nibh erat vel magna.\n" +
                    "Phasellus diam orci, dignissim et, gravida vitae, venenatis eu, elit.\n" +
                    "Suspendisse dictum enim at nisl. Integer magna erat, viverra sit amet, consectetuer nec, accumsan ut, mi.\n" +
                    "Nunc ultricies consectetuer mauris. Nulla lectus mauris, viverra ac, pulvinar a, commodo quis, nulla.\n" +
                    "Ut eget nulla. In est dolor, convallis non, tincidunt vestibulum, porttitor et, eros.\n" +
                    "Donec vehicula ultrices nisl.";

            int chunkLenght = 5;
            int bytesLeft = input.length;
            while (bytesLeft > chunkLenght) {
                trim.write(input, input.length - bytesLeft, chunkLenght);
                bytesLeft -= chunkLenght;
            }
            trim.write(input, input.length - bytesLeft, bytesLeft);

            trim.flush();
            trim.close();

            assertEquals("Should be trimmed", '"' + trimmed + '"', '"' + new String(out.toByteArray()) + '"');
        }

        // TODO: Test that we DON'T remove too much...
    }

    public static final class TrimWSServletResponseWrapperTestCase extends ServletResponseAbsrtactTestCase {
        protected ServletResponse makeServletResponse() {
            return new TrimWhiteSpaceFilter.TrimWSServletResponseWrapper(new MockServletResponse());
        }
    }
}
