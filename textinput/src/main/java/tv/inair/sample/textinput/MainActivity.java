package tv.inair.sample.textinput;

import android.graphics.Color;
import android.os.Bundle;

import inair.app.IAActivity;
import inair.event.Event;
import inair.extra.progresshud.UIProgressHUD;
import inair.input.FocusManager;
import inair.input.IAInputElement;
import inair.input.KeyEventArgs;
import inair.input.KeyboardManager;
import inair.view.UIStackView;
import inair.view.UITextView;
import inair.view.UIView;
import inair.view.UIViewGroup;

public class MainActivity extends IAActivity {

  public static final String TAG = "textinput";

  UIStackView mForm;
  UITextView mUsernameTextView;
  UITextView mPasswordTextView;
  UITextView mSubmit;

  KeyboardManager.EditingSession mSession;

  @Override
  public void onInitialize(Bundle bundle) {
    setRootContentView(R.layout.activity_main);

    mForm = (UIStackView) findUIViewById(R.id.form);
    mUsernameTextView = (UITextView) findUIViewById(R.id.username);
    mPasswordTextView = (UITextView) findUIViewById(R.id.password);
    mSubmit = (UITextView) findUIViewById(R.id.submit);

    mUsernameTextView.gotFocus.addListener(highlightTextView);
    mUsernameTextView.lostFocus.addListener(unhighlightTextView);

    mPasswordTextView.gotFocus.addListener(highlightTextView);
    mPasswordTextView.lostFocus.addListener(unhighlightTextView);

    mSubmit.gotFocus.addListener(highlightTextView);
    mSubmit.lostFocus.addListener(unhighlightTextView);

    // listen to onKeyUp event
    addViewEventListener(UIView.KeyUpEvent, onKeyUp);
  }

  @Override
  public void onRootViewDidAppear() {
    super.onRootViewDidAppear();
    mUsernameTextView.focus();
  }

  private Event.Listener<Void> highlightTextView = new Event.Listener<Void>() {
    @Override
    public void onTrigger(Object sender, Void args) {
      UIViewGroup group = (UIViewGroup) ((UITextView) sender).getParent();
      group.setBorderColor(Color.CYAN);
    }
  };

  private Event.Listener<Void> unhighlightTextView = new Event.Listener<Void>() {
    @Override
    public void onTrigger(Object sender, Void args) {
      UIViewGroup group = (UIViewGroup) ((UITextView) sender).getParent();
      group.setBorderColor(Color.WHITE);
    }
  };

  private Event.Listener<KeyEventArgs> onKeyUp = new Event.Listener<KeyEventArgs>() {
    @Override
    public void onTrigger(Object sender, KeyEventArgs args) {
      if (args.keyCode == KeyEventArgs.KEYCODE_OK) {
        IAInputElement focusedElement = FocusManager.getFocusedElement(getRootView());

        if (focusedElement.equals(mUsernameTextView)) {
          openTextInputForUsernameField();
        } else if (focusedElement.equals(mPasswordTextView)) {
          openTextInputForPasswordField();
        } else if (focusedElement.equals(mSubmit)) {
          submit();
        }
      } else if (args.keyCode == KeyEventArgs.KEYCODE_UP) {
        changeFocus(false);
      } else if (args.keyCode == KeyEventArgs.KEYCODE_DOWN) {
        changeFocus(true);
      }
    }
  };

  private void changeFocus(boolean next) {
    IAInputElement focusedElement = FocusManager.getFocusedElement(getRootView());
    if (focusedElement.equals(mUsernameTextView)) {
      if (next) {
        mPasswordTextView.focus();
      } else {
        mSubmit.focus();
      }
    } else if (focusedElement.equals(mPasswordTextView)) {
      if (next) {
        mSubmit.focus();
      } else {
        mUsernameTextView.focus();
      }
    } else if (focusedElement.equals(mSubmit)) {
      if (next) {
        mUsernameTextView.focus();
      } else {
        mPasswordTextView.focus();
      }
    }
  }

  private void openTextInputForUsernameField() {
    // ask KeyboardManager to open TextInput view and generate new EditingSession
    KeyboardManager keyboardManager = getInAirService(IA_KEYBOARD_SERVICE);
    mSession = keyboardManager.sessionStart(MainActivity.this, mUsernameTextView.getText(), "johndoe");

    // when editing session end, update the text into our TextView
    mSession.onSessionEnd.addListener(new Event.Listener<CharSequence>() {
      @Override
      public void onTrigger(Object sender, CharSequence args) {
        mUsernameTextView.setText(args.toString());
      }
    });
  }

  private void openTextInputForPasswordField() {
    // ask KeyboardManager to open TextInput view and generate new EditingSession
    KeyboardManager keyboardManager = getInAirService(IA_KEYBOARD_SERVICE);
    mSession = keyboardManager.sessionStart(MainActivity.this, mPasswordTextView.getText(), "mysupersecretpassword");

    // when editing session end, update the text into our TextView
    mSession.onSessionEnd.addListener(new Event.Listener<CharSequence>() {
      @Override
      public void onTrigger(Object sender, CharSequence args) {
        mPasswordTextView.setText(args.toString());
      }
    });
  }

  private void submit() {
    // do validation or any business logic here
    UIProgressHUD.with(this)
      .showSuccess("Submitted")
      .in(1000);
  }
}
