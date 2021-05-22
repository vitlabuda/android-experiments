package cz.vitlabuda.test.mvvmnotepad;

import android.view.View;

import com.google.android.material.snackbar.Snackbar;

public final class SnackbarMaker {
    private final View view;

    public SnackbarMaker(View view) {
        this.view = view;
    }

    public void make(String message, int duration) {
        Snackbar.make(view, message, duration).show();
    }

    public void makeShort(String message) {
        make(message, Snackbar.LENGTH_SHORT);
    }
}
