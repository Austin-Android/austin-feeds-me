@Module
public class DataModule {
    private static final String FIREBASE_URL = "https://austin-feeds-me.firebaseio.com/events";

    @Provides @Singleton
    DatabaseReference firebase() {
        return FirebaseDatabase.getInstance().getReferenceFromUrl(FIREBASE_URL);
    }

    @Provides @Singleton
    EventsRepository eventsRepository(FirebaseEventsDataSource firebaseEventsDataSource) {
        return new EventsRepository(firebaseEventsDataSource);
    }

    @Provides @Named("meetup") @Singleton
    EventsDataSource meetupDataSource() {
        return Mockito.mock(MeetupDataSource.class);
    }

    @Provides @Named("eventbrite") @Singleton
    EventsDataSource eventbriteDataSource() {
        return Mockito.mock(EventbriteDataSource.class);
    }
}