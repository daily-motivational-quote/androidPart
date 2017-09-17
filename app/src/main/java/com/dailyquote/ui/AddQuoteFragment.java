package com.dailyquote.ui;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.dailyquote.MainActivity;
import com.dailyquote.R;
import com.dailyquote.network.DailyQuoteApplication;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by stoyan-ivanov on 31.08.17.
 */

public class AddQuoteFragment extends Fragment {
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_add_quote, container, false);


        Button btnUploadQuote = (Button) view.findViewById(R.id.btn_upload_quote);

        btnUploadQuote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                uploadQuoteToDatabase(getTextFromEditText(view));
                showToast();
                goBackToQuoteOfTheDay();
            }
        });

        return view;
    }

    private String getTextFromEditText(View view) {
        final EditText etQuote = (EditText) view.findViewById(R.id.et_add_quote);

        return etQuote.getText().toString();
    }

    private void uploadQuoteToDatabase(String quote) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = database.getReference("quotes");

        databaseReference.setValue(quote);
    }

    private void showToast() {
        Toast toast=Toast.makeText(DailyQuoteApplication.getStaticContext(),"Quote uploaded",Toast.LENGTH_SHORT);
        toast.setMargin(50,50);
        toast.show();
    }

    private void goBackToQuoteOfTheDay() {
        ((MainActivity) getActivity()).attachFragment(new QuoteFragment());
    }
}