package com.nullpointerexception.cicerone.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.model.LatLng;
import com.nullpointerexception.cicerone.R;
import com.nullpointerexception.cicerone.components.AuthenticationManager;
import com.nullpointerexception.cicerone.components.BackEndInterface;
import com.nullpointerexception.cicerone.components.Feedback;
import com.nullpointerexception.cicerone.components.Itinerary;
import com.nullpointerexception.cicerone.components.Stage;
import com.nullpointerexception.cicerone.components.User;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.Vector;

public class DevelopersToolsActivity extends AppCompatActivity
{

    private EditText cityItinerary, dateItinerary;
    private Button generateItinerary, generateFeedback;

    List<User> users = new Vector<>();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_developers_tools);

        cityItinerary = findViewById(R.id.locationGenItinerary);
        dateItinerary = findViewById(R.id.dateGenItinerary);
        generateItinerary = findViewById(R.id.btnGenItinerary);
        generateFeedback = findViewById(R.id.btnGenerateFeedback);

        generateItinerary.setOnClickListener(v -> generateItinerary());
        generateItinerary.setOnLongClickListener(v ->
        {
            dateItinerary.setText("2019/07/07");
            return false;
        });

        generateFeedback.setOnClickListener(v -> generateFeedback());
    }

    private void generateItinerary()
    {
        List<String> places = Arrays.asList("Milano", "Venezia", "Torino", "Londra", "Roma", "Barcellona", "Bari",
                "Canosa di Puglia", "Bitritto", "Napoli", "New York City", "Berlino");

        List<String> stagesList = Arrays.asList("Milano", "Venezia", "Torino", "Londra", "Roma", "Barcellona", "Bari",
                "Canosa di Puglia", "Bitritto", "Napoli", "New York City", "Berlino");

        Random random = new Random();

        Itinerary itinerary = new Itinerary();
        itinerary.setCurrency("€");
        Date currentDate = Calendar.getInstance().getTime();
        currentDate.setMonth( currentDate.getMonth() + random.nextInt(3) );
        currentDate.setDate( random.nextInt(20) );
        if(dateItinerary.getText().toString().isEmpty())
            itinerary.setDate(new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault()).format(currentDate));
        else
            itinerary.setDate(dateItinerary.getText().toString());
        itinerary.setCicerone( AuthenticationManager.get().getUserLogged());
        itinerary.setLanguage("Italiano");
        if(cityItinerary.getText().toString().isEmpty())
        {
            String place = places.get( random.nextInt( places.size()-1 ));
            itinerary.setLocation(place);
            itinerary.setMeetingPlace(place);
            itinerary.setDescription("A little trip in " + place + ", hope you will like it!");
        }
        else
        {
            String city = cityItinerary.getText().toString();
            itinerary.setLocation(city);
            itinerary.setMeetingPlace(city);
            itinerary.setDescription("A little trip in " + city + ", hope you will like it!");
        }

        itinerary.setMaxParticipants(random.nextInt(100));
        itinerary.setPrice( (float) random.nextInt(5000) / 100f);
        itinerary.setMeetingTime((10 + random.nextInt(13)) + ":" + (10 + random.nextInt(49)));

        //  Stages
        List<Stage> stages = new Vector<>();
        int bound = random.nextInt(5) + 1;

        for(int n = 0; n < bound; n++)
        {
            String loc = stagesList.get( random.nextInt( places.size()-1 ));
            LatLng coords = new LatLng((float) random.nextInt(10000) / 100f,
                    (float) random.nextInt(10000) / 100f);
            Stage stage = new Stage(loc, loc, coords);
            stage.setDescription("This nice place will be our next stage.\nIt's such a wonderful place, hope you like it too.");
            stages.add(stage);
        }

        itinerary.setStages(stages);
        itinerary.generateId();

        BackEndInterface.get().storeEntity(itinerary, new BackEndInterface.OnOperationCompleteListener()
        {
            @Override
            public void onSuccess()
            {
                Toast.makeText(DevelopersToolsActivity.this, "Itinerario creato", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError()
            {
                Toast.makeText(DevelopersToolsActivity.this, "Errore", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void getAllUsers()
    {
        BackEndInterface.get().getAllUsers(users, new BackEndInterface.OnOperationCompleteListener()
        {
            @Override
            public void onSuccess()
            {
                generateFeedback();
            }

            @Override
            public void onError()
            {
                Toast.makeText(DevelopersToolsActivity.this, "Errore", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void generateFeedback()
    {
        if(users.isEmpty())
        {
            getAllUsers();
            return;
        }

        Random random = new Random();

        User currentUser = AuthenticationManager.get().getUserLogged();
        User userSender;
        do
        {
            userSender = users.get( random.nextInt(users.size()) );
        } while(userSender.getId().equals(currentUser.getId()));

        Feedback feedback = new Feedback(userSender);

        List<String> comments = Arrays.asList("Nice itineraries, good job!",
                "Great experience!\n" + currentUser.getName() + " thanks a lot for the trip.",
                "I got inspired a lot from the places visited with you, a very instructive trip.\nThanks.",
                "A lot of fun, " + currentUser.getName() + " is a very kind person, and will bring you to see the best places.",
                "The best Cicerone!");

        feedback.setComment( comments.get( random.nextInt(comments.size())) );
        feedback.setVote( random.nextInt(5)+1 );

        if(currentUser.getFeedbacks().contains(feedback))
            currentUser.editFeedback(feedback);
        else
            currentUser.addFeedback(feedback);

        BackEndInterface.get().storeEntity(currentUser, new BackEndInterface.OnOperationCompleteListener()
        {
            @Override
            public void onSuccess()
            {
                Toast.makeText(DevelopersToolsActivity.this, "Feedback generato", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError()
            {
                Toast.makeText(DevelopersToolsActivity.this, "Errore", Toast.LENGTH_SHORT).show();
            }
        });

    }
}
