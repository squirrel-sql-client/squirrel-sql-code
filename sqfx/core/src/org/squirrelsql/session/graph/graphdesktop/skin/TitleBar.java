package org.squirrelsql.session.graph.graphdesktop.skin;

import javafx.beans.value.ObservableValue;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import org.squirrelsql.session.graph.graphdesktop.Window;

/**
 * Created by gerd on 28.03.16.
 */
class TitleBar extends HBox
{

    public static final String DEFAULT_STYLE_CLASS = "window-titlebar";
    private final Pane leftIconPane;
    private final Pane rightIconPane;
    private final Text label = new Text();
    private final double iconSpacing = 3;
    Window control;
    // estimated size of "...",
    // is there a way to find out text dimension without rendering it
    private final double offset = 40;
    private double originalTitleWidth;

    public TitleBar(Window w) {

        this.control = w;

        setManaged(false);

        getStylesheets().setAll(w.getStylesheets());
        getStyleClass().setAll(DEFAULT_STYLE_CLASS);

        setSpacing(8);

//        label.setTextAlignment(TextAlignment.CENTER);
//        label.getStyleClass().setAll(DEFAULT_STYLE_CLASS);

        leftIconPane = new IconPane();
        rightIconPane = new IconPane();

        getChildren().add(leftIconPane);
//        getChildren().add(VFXLayoutUtil.createHBoxFiller());
        getChildren().add(label);
//        getChildren().add(VFXLayoutUtil.createHBoxFiller());
        getChildren().add(rightIconPane);


        control.boundsInParentProperty().addListener(
                (ObservableValue<? extends Bounds> ov, Bounds t, Bounds t1) -> {
            if (control.getTitle() == null
                    || getLabel().getText() == null
                    || getLabel().getText().isEmpty()) {
                return;
            }

            double maxIconWidth = Math.max(
                    leftIconPane.getWidth(), rightIconPane.getWidth());

            if (!control.getTitle().equals(getLabel().getText())) {
                if (originalTitleWidth
                        + maxIconWidth * 2 + offset < getWidth()) {
                    getLabel().setText(control.getTitle());
                }
            } else if (!"...".equals(getLabel().getText())) {
                if (originalTitleWidth
                        + maxIconWidth * 2 + offset >= getWidth()) {
                    getLabel().setText("...");
                }
            }
        });

    }

    public void setTitle(String title) {
        getLabel().setText(title);

        originalTitleWidth = getLabel().getBoundsInParent().getWidth();

        double maxIconWidth = Math.max(
                leftIconPane.getWidth(), rightIconPane.getWidth());

        if (originalTitleWidth
                + maxIconWidth * 2 + offset >= getWidth()) {
            getLabel().setText("...");
        }
    }

    public String getTitle() {
        return getLabel().getText();
    }

    public void addLeftIcon(Node n) {
        leftIconPane.getChildren().add(n);
    }

    public void addRightIcon(Node n) {
        rightIconPane.getChildren().add(n);
    }

    public void removeLeftIcon(Node n) {
        leftIconPane.getChildren().remove(n);
    }

    public void removeRightIcon(Node n) {
        rightIconPane.getChildren().remove(n);
    }

    @Override
    protected double computeMinWidth(double h) {
        double result = super.computeMinWidth(h);

        double iconWidth =
                Math.max(
                leftIconPane.prefWidth(h),
                rightIconPane.prefWidth(h)) * 2;

        result = Math.max(result,
                iconWidth
                //                + getLabel().prefWidth(h)
                + getInsets().getLeft()
                + getInsets().getRight());

        return result + iconSpacing * 2 + offset;
    }

    @Override
    protected double computePrefWidth(double h) {
        return computeMinWidth(h);
    }

    @Override
    protected void layoutChildren() {
        super.layoutChildren();

        leftIconPane.resizeRelocate(getInsets().getLeft(), getInsets().getTop(),
                leftIconPane.prefWidth(USE_PREF_SIZE),
                getHeight() - getInsets().getTop() - getInsets().getBottom());

        rightIconPane.resize(rightIconPane.prefWidth(USE_PREF_SIZE),
                getHeight() - getInsets().getTop() - getInsets().getBottom());
        rightIconPane.relocate(getWidth() - rightIconPane.getWidth() - getInsets().getRight(),
                getInsets().getTop());
    }

    /**
     * @return the label
     */
    public final Text getLabel() {
        return label;
    }

    private static class IconPane extends Pane {

        private final double spacing = 2;

        public IconPane() {
            setManaged(false);
            //
            setPrefWidth(USE_COMPUTED_SIZE);
            setMinWidth(USE_COMPUTED_SIZE);
        }

        @Override
        protected void layoutChildren() {

            int count = 0;

            double width = getHeight();
            double height = getHeight();

            for (Node n : getManagedChildren()) {

                double x = (width + spacing) * count;

                n.resizeRelocate(x, 0, width, height);

                count++;
            }
        }

        @Override
        protected double computeMinWidth(double h) {
            return getHeight() * getChildren().size()
                    + spacing * (getChildren().size() - 1);
        }

        @Override
        protected double computeMaxWidth(double h) {
            return computeMinWidth(h);
        }

        @Override
        protected double computePrefWidth(double h) {
            return computeMinWidth(h);
        }
    }
}
