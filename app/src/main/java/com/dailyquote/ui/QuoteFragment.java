package com.dailyquote.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.dailyquote.R;
import com.dailyquote.network.Quote;
import com.dailyquote.view_utils.CustomTextView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static android.content.ContentValues.TAG;


public class QuoteFragment extends Fragment {

    public static final String MY_PREFERENCES = "MyPrefs" ;
    public static final String DATE_PREFERENCE = "DatePrefs";
    public static final String QUOTE_PREFERENCE = "QuotePrefs";
    public static final String DATE_EXTRA = "currentDate";
    public static final String ALL_QUOTES_PREFERENCE = "AllQuotesPref";
    public static String currDate;
    private View view;

    public static QuoteFragment newInstance(String currDate) {
        QuoteFragment quoteFragment = new QuoteFragment();
        Bundle args = new Bundle();
        args.putString(DATE_EXTRA, currDate);
        quoteFragment.setArguments(args);
        return quoteFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_quote, container, false);

        getExtraArguments();
        displayQuote();

        ImageView options = (ImageView) view.findViewById(R.id.iv_options);

        options.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                // TO DO: IMPLEMENT THIS!
            }
        });

        return view;
    }

    private void getExtraArguments() {
        currDate = getArguments().getString(DATE_EXTRA);
    }

    private void displayQuote() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(MY_PREFERENCES, Context.MODE_PRIVATE);
        String storedDate = sharedPreferences.getString(DATE_PREFERENCE, null);

        if(currDate.equals(storedDate)) {
            String storedQuote = sharedPreferences.getString(QUOTE_PREFERENCE, null);
            setQuote(storedQuote);

        } else {
            getRandomQuote();
        }
    }

    private void getRandomQuote() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = database.getReference("quotes");

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            private String quote;

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot != null) {
                    Random random = new Random();
                    int index = random.nextInt((int) dataSnapshot.getChildrenCount());
                    int count = 0;

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        if (count == index) {
                            quote = snapshot.getValue(String.class);
                            setQuote(quote);
                            savePreferences(quote);
                            saveToAllQuotes(quote);
                            return;
                        }
                        count++;
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.w(TAG, "Failed to read value.", error.toException());
            }

        });
    }

    private void saveToAllQuotes(String quote) {
        SharedPreferences sharedPreferences = this.getActivity().getSharedPreferences(MY_PREFERENCES, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString(ALL_QUOTES_PREFERENCE, null);

        if(json != null) {
            Type type = new TypeToken<List<Quote>>(){}.getType();
            List<Quote> allSavedQuotes = gson.fromJson(json, type);

            allSavedQuotes.add(new Quote(currDate, quote));
            pushToSharedPreferences(allSavedQuotes);
        } else {
            List<Quote> listOfQuotes = new ArrayList<>();
            listOfQuotes.add(new Quote(currDate, quote));
            pushToSharedPreferences(listOfQuotes);
        }

    }

    private void pushToSharedPreferences(List<Quote> listOfQuotes) {
        SharedPreferences sharedPreferences = this.getActivity().getSharedPreferences(MY_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        Gson gson = new Gson();
        String json = gson.toJson(listOfQuotes);
        editor.putString(ALL_QUOTES_PREFERENCE, json);
        editor.commit();
    }

    private void setQuote (String quote) {
        final CustomTextView tv = (CustomTextView) view.findViewById(R.id.tv_quote);
        tv.setText(quote);
    }

    private void savePreferences(String quote) {
        SharedPreferences sharedPreferences = this.getActivity().getSharedPreferences(MY_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor prefEditor = sharedPreferences.edit();

        prefEditor.putString(DATE_PREFERENCE, currDate);
        prefEditor.putString(QUOTE_PREFERENCE, quote);
        prefEditor.apply();
    }
}