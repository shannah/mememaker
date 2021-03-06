package com.codename1.demos.mememaker;


import com.codename1.components.InteractionDialog;
import com.codename1.components.MultiButton;
import com.codename1.components.SpanButton;
import com.codename1.components.SpanLabel;
import com.codename1.components.ToastBar;
import com.codename1.io.FileSystemStorage;
import com.codename1.ui.Display;
import com.codename1.ui.Form;
import com.codename1.ui.Dialog;
import com.codename1.ui.Label;
import com.codename1.ui.plaf.UIManager;
import com.codename1.ui.util.Resources;
import com.codename1.io.Log;
import com.codename1.ui.Button;
import com.codename1.ui.Command;
import com.codename1.ui.Component;
import com.codename1.ui.Container;
import com.codename1.ui.Font;
import com.codename1.ui.Graphics;
import com.codename1.ui.Image;
import com.codename1.ui.Slider;
import com.codename1.ui.Stroke;
import com.codename1.ui.TTFFont;
import com.codename1.ui.TextField;
import com.codename1.ui.Toolbar;
import com.codename1.ui.events.ActionEvent;
import com.codename1.ui.events.ActionListener;
import com.codename1.ui.layouts.BorderLayout;
import com.codename1.ui.layouts.BoxLayout;
import com.codename1.ui.layouts.LayeredLayout;
import com.codename1.ui.plaf.Style;
import com.codename1.ui.util.EventDispatcher;
import com.codename1.ui.util.ImageIO;
import com.codename1.ui.util.UITimer;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.TimerTask;

/**
 * This file was generated by <a href="https://www.codenameone.com/">Codename One</a> for the purpose 
 * of building native mobile applications using Java.
 */
public class MemeMaker {

    private Form current;
    private Resources theme;
    private ViewFactory viewFactory = new ViewFactory();
    private Controller controller = new Controller();
    private Resources css;
    private Container mainView;
    

    public void init(Object context) {
        theme = UIManager.initFirstTheme("/theme");
        try {
            css = Resources.openLayered("/theme.css");
            UIManager.getInstance().addThemeProps(css.getTheme(css.getThemeResourceNames()[0]));
        } catch (Exception ex) {
            Log.e(ex);
        }

        // Enable Toolbar on all Forms by default
        Toolbar.setGlobalToolbar(true);

        // Pro only feature, uncomment if you have a pro subscription
        // Log.bindCrashProtection(true);
    }
    EventDispatcher imageSelectedListeners;
    private void fireImageSelected(String file) {
        if (imageSelectedListeners != null) {
            imageSelectedListeners.fireActionEvent(new ActionEvent(file));
        }
    }
    
    private void addImageSelectedListener(ActionListener l) {
        if (imageSelectedListeners == null) {
            imageSelectedListeners = new EventDispatcher();
        }
        imageSelectedListeners.addListener(l);
    }
    
    private void removeImageSelectedListener(ActionListener l) {
        if (imageSelectedListeners != null) {
            imageSelectedListeners.removeListener(l);
        }
    }
    
    public void start() {
        Display disp = Display.getInstance();
        String arg = disp.getProperty("AppArg", null);
        if (arg != null && FileSystemStorage.getInstance().exists(arg)) {
            // The app started with an argument - probably a path to an image
            // to use as the basis for a meme.
            
            // Reset the app arg so that we won't process it again next time the
            // app is brought to the foreground.
            disp.setProperty("AppArg", null);
            
            // We will use a timer to load the image after the UI has already been set up.
            java.util.Timer timer = new java.util.Timer();
            timer.schedule(new TimerTask() {

                @Override
                public void run() {
                    //System.out.println("In timer callback");
                    Display.getInstance().callSerially(()->{
                        fireImageSelected(arg);
                    });
                }
                
                }
                , 500
                    
            );
            
        }
        
        if(current != null){
            current.show();
            return;
        }
        Form hi = new Form("MemeMaker");
        hi.setLayout(new BorderLayout());
        
        mainView = viewFactory.createMainView(null);
        hi.addComponent(BorderLayout.CENTER, mainView);
        
        hi.show();
        
        Toolbar tb = new Toolbar();
        hi.setToolbar(tb);
        
        // A menu option to export the meme as an image.
        tb.addCommandToOverflowMenu(new Command("Export as Image") {

            @Override
            public void actionPerformed(ActionEvent evt) {
                Display.getInstance().callSerially(()->{
                    controller.exportAsImage();
                });
                
            }
            
        });
        
        tb.addCommandToOverflowMenu(new Command("About Meme Maker") {

            @Override
            public void actionPerformed(ActionEvent evt) {
                controller.showAboutForm();
            }
            
        });
        
        // A menu option to share the meme to another app (e.g. Facebook)
        tb.addCommandToOverflowMenu(new Command("Share") {

            @Override
            public void actionPerformed(ActionEvent evt) {
                Display.getInstance().callSerially(()->{
                    controller.share();
                });
                
           
                
            }
            
        });
        
        // A dummy command to display the MemeMaker icon on the title bar
        tb.addCommandToLeftBar("Meme Maker", UIManager.getInstance().getThemeImageConstant("MemeIconImage"), e->{
            
        });
        
        
        //tb.setTitle("Meme Maker");
        //tb.setTitleCentered(true);
    }

    public void stop() {
        current = Display.getInstance().getCurrent();
        if(current instanceof Dialog) {
            ((Dialog)current).dispose();
            current = Display.getInstance().getCurrent();
        }
    }
    
    public void destroy() {
    }

    
    
    /**
     * Writes the specified component to an image.
     * @param component The component to write.
     * @return The image with the content of the component.
     */
    private Image createImage(Component component) {
        Image img = Image.createImage(component.getWidth(), component.getHeight());
        Graphics g = img.getGraphics();
        g.translate(-component.getX(), -component.getY());
        component.paint(g);
        return img;
    }
    
    // Reference to the currently shown text properties dialog.
    private InteractionDialog currDialog;
    
    // Reference to the current pointerPressedListener that is installed
    // by the text properties dialog to block events while dialog is open.
    private ActionListener textPropertiesFormPressedListener;
    
    
    /**
     * Internal class with "controller" style methods.  Used only for code structure
     */
    class Controller {
        
        
        /**
         * Shows the about form
         */
        void showAboutForm() {
            Form about = new Form("About");
            about.setLayout(new BoxLayout(BoxLayout.Y_AXIS));
            SpanLabel l1 = new SpanLabel("Meme Maker allows you to create memes and share them to your social media feeds.");
            
            SpanLabel l2 = new SpanLabel("This application was developed by Steve Hannah using Codename One.");
            
            SpanLabel l3 = new SpanLabel("Copyright (c) 2016 Codename One.  All Rights Reserved");
            
            Button b1 = new Button("View the Source Code");
            b1.addActionListener(e->{
               Display.getInstance().execute("https://github.com/shannah/mememaker");
            });
            
            
            
            Button b2 = new Button("Visit Codename One Website");
            b2.addActionListener(e->{
                Display.getInstance().execute("https://www.codenameone.com");
            });
            
            about.add(l1).add(l2).add(l3).add(b1).add(b2);
            
            Form curr = Display.getInstance().getCurrent();
            Command back = new Command("Back") {

                @Override
                public void actionPerformed(ActionEvent evt) {
                    curr.showBack();
                }
                
            };
            
            
            Toolbar tb = new Toolbar();
            about.setToolbar(tb);
            tb.setBackCommand(back);
            tb.setTitle("About Meme Maker");
            tb.setTitleCentered(true);
            
            about.show();
            
        }
        
        /**
         * Exports the current meme as an image.
         */
        void exportAsImage() {
            Image img = createImage((Container)mainView.getClientProperty("canvas"));
            ToastBar.Status status = ToastBar.getInstance().createStatus();
            status.setMessage("Writing image ...");
            status.setShowProgressIndicator(true);
            status.showDelayed(100);
            FileSystemStorage fs = FileSystemStorage.getInstance();
            String imgPath = fs.getAppHomePath()+fs.getFileSystemSeparator()+"export.png";
            boolean[] success = new boolean[1];
            Runnable rMakeImage = ()->{
                ImageIO io = ImageIO.getImageIO();

                try (OutputStream os = fs.openOutputStream(imgPath)){
                    io.save(img, os, ImageIO.FORMAT_PNG, 1f);
                    success[0] = true;
                } catch (Exception ex) {
                    Log.e(ex);
                    Display.getInstance().callSerially(()->{
                        ToastBar.showErrorMessage("Failed to generate image.");
                    });


                }
            };
            try {
                if ("ios".equals(Display.getInstance().getPlatformName()) && !Display.getInstance().isSimulator()) {
                    rMakeImage.run();
                } else {
                    Display.getInstance().invokeAndBlock(rMakeImage);
                }
                if (!success[0]) {
                    
                    return;
                }
            } finally {
                
                status.clear();
            }
            
            // Try to open the image in an appropriate program
            Display.getInstance().execute(imgPath);
        }
        
        /**
         * Shares the current meme.
         */
        void share() {
            ToastBar.Status status = ToastBar.getInstance().createStatus();
            status.setMessage("Writing image ...");
            status.setShowProgressIndicator(true);
            FileSystemStorage fs = FileSystemStorage.getInstance();
            String imgPath = fs.getAppHomePath()+fs.getFileSystemSeparator()+"export.png";
            status.showDelayed(100);
            try {
                Image img = createImage((Container)mainView.getClientProperty("canvas"));
                ImageIO io = ImageIO.getImageIO();
                
                boolean[] success = new boolean[1];
                
                Runnable rMakeImage = ()->{
                    try (OutputStream os = fs.openOutputStream(imgPath)){
                        io.save(img, os, ImageIO.FORMAT_PNG, 1f);
                        success[0] = true;
                    } catch (Exception ex) {
                        Log.e(ex);
                        Display.getInstance().callSerially(()-> {
                            ToastBar.showErrorMessage("Failed to generate image.");
                        });


                    }
                };
                if ("ios".equals(Display.getInstance().getPlatformName()) && !Display.getInstance().isSimulator()) {
                    rMakeImage.run();
                } else {
                    Display.getInstance().invokeAndBlock(rMakeImage);
                }
                if (!success[0]) {
                    return;
                }
            } finally {
                status.clear();
            }
                    
            // Try to open the image in an appropriate program
            Display.getInstance().share(null, imgPath, "image/png");
        }
        
        /**
         * Shows the text properties dialog to edit a given button's text.
         * @param memeText 
         * 
         * @see #controller
         */
        void showTextPropertiesDialog(SpanButton memeText) {
            memeText.setEnabled(false);
            InteractionDialog dlg = new InteractionDialog("Edit Text") {

                @Override
                public void dispose() {
                    memeText.setEnabled(true);
                    super.dispose(); //To change body of generated methods, choose Tools | Templates.
                }
                
            };
            TextField fld = new TextField(memeText.getText());
            
            fld.addDataChangedListener((type, index)->{
                memeText.setText(fld.getText().toUpperCase());
                memeText.getParent().revalidate();
            });
            
            Label fontSizeLabel = new Label("\uf034");
            
            
            TTFFont fawesome = TTFFont.getFont("fontawesome-webfont", (float)Display.getInstance().convertToPixels(3));
            fawesome = fawesome
                    .deriveAntialias(true)
                    .deriveFilled(true, 0xffffff)
                    .deriveStroked(new Stroke(1f, Stroke.CAP_BUTT, Stroke.JOIN_MITER, 1f), 0x0)

                    ;
            

            fontSizeLabel.getAllStyles().setFont(fawesome);

            Slider fontSizeSlider = new Slider();
            fontSizeSlider.setEditable(true);
            fontSizeSlider.setIncrements(1);
            fontSizeSlider.setMinValue(5);
            fontSizeSlider.setMaxValue(Display.getInstance().getDisplayHeight()/4);
            fontSizeSlider.setProgress(memeText.getTextStyle().getFont().getHeight());
            //fontSizeSlider.getAllStyles().setMargin(Component.TOP, 1);
            fontSizeSlider.getAllStyles().setMarginTop(Display.getInstance().convertToPixels(3));
            
            fontSizeSlider.addDataChangedListener((type, index)->{
                Font font = memeText.getTextStyle().getFont();
                font = font.derive(index, 0);
                memeText.getTextAllStyles().setFont(font);
                String text = memeText.getText();
                memeText.setText("");
                memeText.setText(text);
            });
            
            
            // Just some tests to see if the font from CN1FontBox was the same as using system fonts
            //Label l2 = new Label("\uf034");
            //l2.getAllStyles().setFont(Font.createTrueTypeFont("fontawesome-webfont", "fontawesome-webfont.ttf").derive(Display.getInstance().convertToPixels(10), 0));
            //l2.setLegacyRenderer(true);
            
            Container fontSizeRow = BorderLayout.center(fontSizeSlider).add(BorderLayout.WEST, fontSizeLabel);
            
            Label textWidthLabel = new Label("\uf035");
            textWidthLabel.getAllStyles().setFont(fawesome);
            Slider textWidthSlider = new Slider();
            textWidthSlider.setEditable(true);
            textWidthSlider.setIncrements(1);
            textWidthSlider.setMinValue(5);
            textWidthSlider.setMaxValue(200);
            textWidthSlider.setProgress(100);
            textWidthSlider.addDataChangedListener((type, index)->{
                Font font = memeText.getTextStyle().getFont();
                font = ((TTFFont)font).deriveScaled(index/(float)100, 1f);
                memeText.getTextAllStyles().setFont(font);
                String text = memeText.getText();
                memeText.setText("");
                memeText.setText(text);
            });
            textWidthSlider.getAllStyles().setMarginTop(Display.getInstance().convertToPixels(3));
            
            
            Container textWidthRow = BorderLayout.center(textWidthSlider).add(BorderLayout.WEST, textWidthLabel);
            
            dlg.getContentPane().setLayout(new BoxLayout(BoxLayout.Y_AXIS));
            dlg.getContentPane().add(fld).add(fontSizeRow).add(textWidthRow);
            
            Form currForm = Display.getInstance().getCurrent();
            
            
            if (currForm != null) {
                if (textPropertiesFormPressedListener != null) {
                    currForm.removePointerPressedListener(textPropertiesFormPressedListener);
                }
                textPropertiesFormPressedListener = e->{
                    
                    if (textPropertiesFormPressedListener != null && !dlg.contains(e.getX(), e.getY())) {
                        currForm.removePointerPressedListener(textPropertiesFormPressedListener);
                        textPropertiesFormPressedListener = null;
                        
                        dlg.dispose();
                        currDialog = null;
                        memeText.revalidate();
                        memeText.setEnabled(true);
                    }
                };
                currForm.addPointerPressedListener(textPropertiesFormPressedListener);
            }
            
            if (currDialog != null) {
                currDialog.dispose();
            }
            currDialog = dlg;
            dlg.showPopupDialog(memeText);
            fld.startEditingAsync();
        }
    }
    
    /**
     * Internal class to create views.  For code structure only.
     * @see #viewFactory
     */
    class ViewFactory {
        
        Container createMainView(String imagePath) {
            Container root = new Container(new BorderLayout());
            root.getAllStyles().setBgColor(0x0);
            root.getAllStyles().setBgTransparency(255);
            Container canvas = new Container(new LayeredLayout());
            root.putClientProperty("canvas", canvas);
            canvas.getAllStyles().setBgColor(0x0);
            canvas.getAllStyles().setBgTransparency(255);
            canvas.getAllStyles().setBackgroundType(Style.BACKGROUND_NONE);
            SpanButton topButton = new SpanButton("TOP TEXT");
            topButton.setUIID("MemeTextArea");
            topButton.setTextUIID("MemeText");
            topButton.getAllStyles().setMargin(0,0,0,0);
            topButton.getAllStyles().setPadding(0,0,0,0);
            topButton.getTextAllStyles().setMargin(0,0,0,0);
            topButton.getTextAllStyles().setPadding(0,0,0,0);
            TTFFont ttfFont = null;
            try {
                ttfFont = TTFFont.createFont("Coda-Heavy", "/Coda-Heavy.ttf")
                        .deriveFont(Display.getInstance().convertToPixels(6), 1f, 1f, new Stroke(2f, Stroke.CAP_SQUARE, Stroke.JOIN_ROUND, 10f), 0xffffff, 0x0, true, true);
                topButton.getTextAllStyles().setFont(ttfFont);
            } catch (Exception ex) {
                Log.e(ex);
            }
            topButton.getTextAllStyles().setFgColor(0xffffff);
            topButton.getAllStyles().setBgColor(0x0);
            topButton.getAllStyles().setBgTransparency(0);
            
            topButton.addActionListener(e->{
                controller.showTextPropertiesDialog(topButton);
            });
            SpanButton bottomButton = new SpanButton("BOTTOM TEXT");
            bottomButton.setUIID("MemeTextArea");
            bottomButton.setTextUIID("MemeText");
            bottomButton.getTextAllStyles().setFont(ttfFont);
            bottomButton.getAllStyles().setMargin(0,0,0,0);
            bottomButton.getAllStyles().setPadding(0,0,0,0);
            bottomButton.getTextAllStyles().setMargin(0,0,0,0);
            bottomButton.getTextAllStyles().setPadding(0,0,0,0);
            bottomButton.getTextAllStyles().setFgColor(0xffffff);
            bottomButton.addActionListener(e->{
                controller.showTextPropertiesDialog(bottomButton);
            });
            Button imageViewer = new Button("Click to Select Photo");
            
            ActionListener imageSelectedListener = e2->{
                if (e2 != null && e2.getSource() != null) {
                
                    FileSystemStorage fs = FileSystemStorage.getInstance();
                    if (fs.exists((String)e2.getSource())) {
                        try (InputStream imageStream = fs.openInputStream((String)e2.getSource())){
                            Image image = Image.createImage(imageStream);
                            image = image.scaledWidth(canvas.getWidth());
                            if (image.getHeight() > root.getHeight()) {
                                image = image.scaledHeight(root.getHeight());
                            }
                            imageViewer.setIcon(image);
                            imageViewer.setText(null);
                            imageViewer.getParent().revalidate();
                            canvas.getAllStyles().setMarginUnit(
                                    Style.UNIT_TYPE_PIXELS, 
                                    Style.UNIT_TYPE_PIXELS, 
                                    Style.UNIT_TYPE_PIXELS, 
                                    Style.UNIT_TYPE_PIXELS
                            );
                            int hdiff = (root.getHeight() - image.getHeight());
                            int wdiff = (root.getWidth() - image.getWidth())/2;
                            canvas.getAllStyles().setMargin(0, hdiff, wdiff, wdiff);
                            root.revalidate();
                            root.getComponentForm().revalidate();
                            UITimer.timer(500, false, ()->{
                                root.forceRevalidate();
                            });

                        } catch (Exception ex) {
                            Log.e(ex);
                        }
                    }

                }
            };
            
            addImageSelectedListener(imageSelectedListener);
            
            imageViewer.setUIID("ImageViewer");
            imageViewer.getAllStyles().setBgTransparency(0);
            imageViewer.getAllStyles().setBackgroundType(Style.BACKGROUND_NONE);
            imageViewer.getAllStyles().setFgColor(0xffffff);
            imageViewer.getAllStyles().setAlignment(Component.CENTER);
            imageViewer.addActionListener(e-> {
                if (topButton.contains(e.getX(), e.getY()) || bottomButton.contains(e.getX(), e.getY()) || (currDialog != null && currDialog.contains(e.getX(), e.getY()))) {
                    return;
                }
                Display.getInstance().openGallery(e2->{
                    if (e2 != null) {
                        fireImageSelected((String)e2.getSource());
                    }
                }, Display.GALLERY_IMAGE);
                
            });
            
            canvas.addComponent(BorderLayout.center(imageViewer));
            canvas.addComponent(BorderLayout.north(topButton).add(BorderLayout.SOUTH, bottomButton));
            
            if (imagePath != null) {
                Display.getInstance().scheduleBackgroundTask(()->{
                    FileSystemStorage fs = FileSystemStorage.getInstance();
                    if (fs.exists(imagePath)) {
                        try (InputStream imageStream = fs.openInputStream(imagePath)){
                            Image image = Image.createImage(imageStream);
                            ImageIO imageIO = ImageIO.getImageIO();
                            Display.getInstance().callSerially(()->{
                                imageViewer.setIcon(image);
                                imageViewer.setText(null);
                                root.revalidate();
                            });
                        } catch (Exception ex) {
                            Log.e(ex);
                        }
                    }
                });
            }
            
            root.addComponent(BorderLayout.CENTER, canvas);
            return root;
        }
    }
}
