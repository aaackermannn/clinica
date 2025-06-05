package com.raven.clinic;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;

public class RegistrationSuccessDialogFragment extends DialogFragment {

    public RegistrationSuccessDialogFragment() {
        // Пустой конструктор — обязательно для фрагмента
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Убираем Title из диалога (чтобы не было дефолтного заголовка)
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        }
        View view = inflater.inflate(R.layout.dialog_registration_success, container, false);

        Button btnLogin = view.findViewById(R.id.btnDialogLogin);
        btnLogin.setOnClickListener(v -> {
            // При нажатии на «Войти» просто закрываем диалог, и дальше SignUpActivity может вызвать LoginActivity
            dismiss();
            // Можно сообщить Activity, что нужно перейти на экран входа:
            if (getActivity() instanceof SignUpActivity) {
                ((SignUpActivity) getActivity()).navigateToLoginAfterDialog();
            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        // Сделаем фон диалога полупрозрачным по углам (если нужно) и ширину под контент
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow()
                    .setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }

    @Override
    public void onCancel(@NonNull DialogInterface dialog) {
        super.onCancel(dialog);
        // Если пользователь закрыл диалог «назад», то всё равно переходим к экрану входа
        if (getActivity() instanceof SignUpActivity) {
            ((SignUpActivity) getActivity()).navigateToLoginAfterDialog();
        }
    }
}

