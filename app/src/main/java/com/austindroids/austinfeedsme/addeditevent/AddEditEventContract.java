package com.austindroids.austinfeedsme.addeditevent;

/**
 * This specifies the contract between the view and the presenter.
 */
public interface AddEditEventContract {

    interface View {

        void showEmptyTaskError();

        void showEventsList();

        void setTitle(String title);

        void setDescription(String description);

    }

    interface Presenter {

        void createEvent(String uid, String title, String description);

    }
}
