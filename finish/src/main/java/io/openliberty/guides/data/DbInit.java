package io.openliberty.guides.data;

import jakarta.annotation.PostConstruct;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import jakarta.inject.Inject;

@Startup
@Singleton
public class DbInit {

    @Inject
    Packages packages;

    @PostConstruct
    public void init() {
        //Liberty Dev mode restarts the app without restarting the JVM, which results in 
        //the Db not being cleared from memory, so only add the packages if nothing exists.
       if (packages.findAll().count() == 0) {
            packages.insert(new Package(1, 10f, 20f, 10f, "Rochester"));
            packages.insert(new Package(2, 30f, 10f, 10f, "Austin"));
            packages.insert(new Package(3, 5f, 10f, 5f, "RTP"));
            packages.insert(new Package(4, 24f, 15f, 6f, "Rochester"));
            packages.insert(new Package(5, 15f, 7f, 2f, "Austin"));
            packages.insert(new Package(6, 8f, 5f, 3f, "Rochester"));
            packages.insert(new Package(7, 16f, 3f, 15f, "RTP"));
            packages.insert(new Package(8, 2f, 15f, 18f, "Rochester"));

        }
    }
}
