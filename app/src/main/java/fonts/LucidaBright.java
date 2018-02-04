package fonts;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by Huzy_Kamz on 2/3/2018.
 */

public class LucidaBright extends TextView {

    public LucidaBright(Context context) {
        super(context);

        applyCustomFont(context);
    }

    public LucidaBright(Context context, AttributeSet attrs) {
        super(context, attrs);

        applyCustomFont(context);
    }

    public LucidaBright(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        applyCustomFont(context);
    }

    private void applyCustomFont(Context context) {
        Typeface customFont = FontCache.getTypeface("LucidaBrightRegular.ttf", context);
        setTypeface(customFont);
    }
}
