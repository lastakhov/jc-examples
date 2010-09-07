package nl.jamiecraane.vaadinsamples;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import com.lowagie.text.Document;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;
import com.vaadin.Application;
import com.vaadin.terminal.Resource;
import com.vaadin.terminal.StreamResource;
import com.vaadin.terminal.StreamResource.StreamSource;
import com.vaadin.ui.Button;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;

public class VaadinsamplesApplication extends Application {
    private final GridLayout mainLayout = new GridLayout(1, 2);

    @Override
    public void init() {
        final Window mainWindow = new Window("Vaadinsamples Application");
        mainLayout.setRowExpandRatio(1, 1f);
        mainLayout.setSizeFull();
        mainWindow.setContent(mainLayout);

        // The embedded will hold out pdf contents
        final Embedded pdfContents = new Embedded();
        pdfContents.setSizeFull();
        pdfContents.setType(Embedded.TYPE_BROWSER);

        CssLayout vl = new CssLayout();
        vl.addComponent(new Button("PDF in popup", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                // Remove the embedded in the mainlayout when showing the popup.
                // The pdfreader always show on top of the other divs.
                mainLayout.removeComponent(pdfContents);
                displayPopup();
            }
        }));
        vl.addComponent(new Button("PDF in mainlayout", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                Resource pdf = createPdf();
                pdfContents.setSource(pdf);
                try {
                    mainLayout.addComponent(pdfContents, 0, 1, 0, 1);
                } catch (IllegalArgumentException e) {

                }
            }
        }));
        mainWindow.addComponent(vl);

        setMainWindow(mainWindow);
    }

    private Resource createPdf() {
        // Here we create a new StreamResource which downloads our StreamSource,
        // which is our pdf.
        StreamResource resource = new StreamResource(new Pdf(), "test.pdf?" + System.currentTimeMillis(), this);
        // Set the right mime type
        resource.setMIMEType("application/pdf");
        return resource;
    }

    private void displayPopup() {
        Window window = new Window();
        ((VerticalLayout) window.getContent()).setSizeFull();
        window.setResizable(true);
        window.setWidth("800");
        window.setHeight("600");
        window.center();
        Embedded e = new Embedded();
        e.setSizeFull();
        e.setType(Embedded.TYPE_BROWSER);

        // Here we create a new StreamResource which downloads our StreamSource,
        // which is our pdf.
        StreamResource resource = new StreamResource(new Pdf(), "test.pdf?" + System.currentTimeMillis(), this);
        // Set the right mime type
        resource.setMIMEType("application/pdf");

        e.setSource(resource);
        window.addComponent(e);
        getMainWindow().addWindow(window);
    }

    /**
     * This class creates a PDF with the iText library. This class implements
     * the StreamSource interface which defines the getStream method.
     */
    public static class Pdf implements StreamSource {
        private final ByteArrayOutputStream os = new ByteArrayOutputStream();

        public Pdf() {
            Document document = null;

            try {
                document = new Document(PageSize.A4, 50, 50, 50, 50);
                PdfWriter.getInstance(document, os);
                document.open();

                document.add(new Paragraph("This is some content for the sample PDF!"));
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (document != null) {
                    document.close();
                }
            }
        }

        @Override
        public InputStream getStream() {
            // Here we return the pdf contents as a byte-array
            return new ByteArrayInputStream(os.toByteArray());
        }
    }
}
