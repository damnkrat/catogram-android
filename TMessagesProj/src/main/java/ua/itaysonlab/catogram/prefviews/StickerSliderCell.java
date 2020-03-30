package ua.itaysonlab.catogram.prefviews;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.TextPaint;
import android.view.Gravity;
import android.widget.FrameLayout;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.ThemePreviewMessagesCell;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.SeekBarView;

import ua.itaysonlab.tgkit.preference.types.TGKitSliderPreference;

public class StickerSliderCell extends FrameLayout {
    private StickerPreviewMessagesCell messagesCell;
    private TGKitSliderPreference.TGSLContract contract;
    private SeekBarView sizeBar;

    private int startRadius;
    private int endRadius;

    private TextPaint textPaint;

    public StickerSliderCell setContract(TGKitSliderPreference.TGSLContract contract) {
        this.contract = contract;
        this.startRadius = contract.getMin();
        this.endRadius = contract.getMax();
        return this;
    }

    public StickerSliderCell(Context context) {
        super(context);

        setWillNotDraw(false);

        textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setTextSize(AndroidUtilities.dp(16));

        sizeBar = new SeekBarView(context);
        sizeBar.setReportChanges(true);
        sizeBar.setDelegate(new SeekBarView.SeekBarViewDelegate() {
            @Override
            public void onSeekBarDrag(boolean stop, float progress) {
                contract.setValue(Math.round(startRadius + (endRadius - startRadius) * progress));
                requestLayout();
                messagesCell.invalidate();
            }

            @Override
            public void onSeekBarPressed(boolean pressed) {

            }
        });
        addView(sizeBar, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, 38, Gravity.START | Gravity.TOP, 5, 5, 39, 0));

        messagesCell = new StickerPreviewMessagesCell(context);
        addView(messagesCell, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, Gravity.LEFT | Gravity.TOP, 0, 53, 0, 0));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        textPaint.setColor(Theme.getColor(Theme.key_windowBackgroundWhiteValueText));
        canvas.drawText("" + contract.getPreferenceValue(), getMeasuredWidth() - AndroidUtilities.dp(39), AndroidUtilities.dp(28), textPaint);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.EXACTLY), heightMeasureSpec);
        sizeBar.setProgress((contract.getPreferenceValue() - startRadius) / (float) (endRadius - startRadius));
    }

    @Override
    public void invalidate() {
        super.invalidate();
        sizeBar.invalidate();
    }
}