package eu.ha3.mc.gui;

@FunctionalInterface
public interface HSliderListener {
    void sliderValueChanged(HGuiSliderControl slider, float value);

    default void sliderPressed(HGuiSliderControl hGuiSliderControl) {

    }

    default void sliderReleased(HGuiSliderControl hGuiSliderControl) {

    }
}
