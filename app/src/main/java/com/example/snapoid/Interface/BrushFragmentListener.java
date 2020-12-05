package com.example.snapoid.Interface;

public interface BrushFragmentListener   {
    void onBrushSizeChangedListener(float size);
    void OnBrushOpacityChangedListener(int opacity);
    void OnBrushColorChangedListener(int color);
    void onBrushStateChangedListener(boolean isEraser);

    void onBrushColorChangedListener(int color);

    void onBrushOpacityChangedListener(int progress);
}
