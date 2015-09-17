package fr.plnech.igem.ui.model;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.ContentViewEvent;
import fr.plnech.igem.ui.HomeActivity;
import io.fabric.sdk.android.Fabric;

/**
 * Created by PLNech on 14/09/2015.
 */
public abstract class LoggedActivity extends AppCompatActivity {
    private static final String TAG = "LoggedActivity";

    protected void logView() {
        final Resources resources = getResources();
        final String contentName = resources.getString(getTitleResId());
        final String contentType = getContentType();
        final String contentId = resources.getResourceName(getLayoutResId()).replace("fr.plnech.igem:layout/", "");
        logView(contentName, contentType, contentId, this);
    }

    public static void logView(String contentName, String contentType, String contentId, Context c) {
        if (!Fabric.isInitialized()) HomeActivity.initFabric(c);
        Answers.getInstance().logContentView(new ContentViewEvent()
                .putContentType(contentType)
                .putContentName(contentName)
                .putContentId(contentId));
        Log.d(TAG, "logView - " + contentType + ": " + contentName + "(" + contentId + ")");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResId());
        logView();
    }

    abstract String getContentType();

    public abstract int getTitleResId();
    public abstract int getLayoutResId();
}
