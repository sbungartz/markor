/*#######################################################
 *
 *   Maintained by Gregor Santner, 2017-
 *   https://gsantner.net/
 *
 *   License of this file: Apache 2.0 (Commercial upon request)
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
#########################################################*/
package net.gsantner.markor.ui.hleditor;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.DrawableRes;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.Toast;

import net.gsantner.markor.R;
import net.gsantner.markor.format.general.CommonTextActions;
import net.gsantner.markor.model.Document;
import net.gsantner.markor.util.ActivityUtils;
import net.gsantner.markor.util.AppSettings;


@SuppressWarnings("WeakerAccess")
public abstract class TextActions {
    protected HighlightingEditor _hlEditor;
    protected Document _document;
    protected Activity _activity;
    protected Context _context;
    protected AppSettings _appSettings;
    protected ActivityUtils _au;
    private int _textActionSidePadding;

    public TextActions(Activity activity, Document document) {
        _document = document;
        _activity = activity;
        _au = new ActivityUtils(activity);
        _context = activity != null ? activity : _hlEditor.getContext();
        _appSettings = new AppSettings(_context);
        _textActionSidePadding = (int) (_appSettings.getEditorTextActionItemPadding() * _context.getResources().getDisplayMetrics().density);
    }

    public abstract void appendTextActionsToBar(ViewGroup viewGroup);

    public View.OnLongClickListener getLongListenerShowingToastWithText(final String text) {
        return v -> {
            try {
                if (!TextUtils.isEmpty(text)) {
                    Toast.makeText(_activity, text, Toast.LENGTH_SHORT).show();
                }
            } catch (Exception ignored) {
            }
            return true;
        };
    }

    protected void appendTextActionToBar(ViewGroup barLayout, @DrawableRes int iconRes, final View.OnClickListener listener, final View.OnLongClickListener longClickListener) {
        ImageView btn = (ImageView) _activity.getLayoutInflater().inflate(R.layout.quick_keyboard_button, null);
        btn.setImageResource(iconRes);
        btn.setOnClickListener(v -> {
            try {
                listener.onClick(v);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
        if (longClickListener != null) {
            btn.setOnLongClickListener(v -> {
                try {
                    longClickListener.onLongClick(v);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                return true;
            });
        }
        btn.setPadding(_textActionSidePadding, btn.getPaddingTop(), _textActionSidePadding, btn.getPaddingBottom());

        boolean isDarkTheme = AppSettings.get().isDarkThemeEnabled();
        btn.setColorFilter(ContextCompat.getColor(_context,
                isDarkTheme ? android.R.color.white : R.color.grey));
        barLayout.addView(btn);
    }

    protected void setBarVisible(ViewGroup barLayout, boolean visible) {
        if (barLayout.getId() == R.id.document__fragment__edit__text_actions_bar && barLayout.getParent() instanceof HorizontalScrollView) {
            ((HorizontalScrollView) barLayout.getParent())
                    .setVisibility(visible ? View.VISIBLE : View.GONE);
        }
    }

    //
    //
    //
    //
    public HighlightingEditor getHighlightingEditor() {
        return _hlEditor;
    }

    public TextActions setHighlightingEditor(HighlightingEditor hlEditor) {
        _hlEditor = hlEditor;
        return this;
    }

    public Document getDocument() {
        return _document;
    }

    public TextActions setDocument(Document document) {
        _document = document;
        return this;
    }

    public Activity getActivity() {
        return _activity;
    }

    public TextActions setActivity(Activity activity) {
        _activity = activity;
        return this;
    }

    public Context getContext() {
        return _context;
    }

    public TextActions setContext(Context context) {
        _context = context;
        return this;
    }

    /**
     * Callable from background thread!
     */
    public void setEditorTextAsync(final String text) {
        _activity.runOnUiThread(() -> _hlEditor.setText(text));
    }

    protected boolean runCommonTextAction(String action) {
        return new CommonTextActions(_activity, _document, _hlEditor).runAction(action);
    }

    public boolean runAction(final String action) {
        return runAction(action, false, null);
    }

    public abstract boolean runAction(final String action, boolean modLongClick, String anotherArg);
}
