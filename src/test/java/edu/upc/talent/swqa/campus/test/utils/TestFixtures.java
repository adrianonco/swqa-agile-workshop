package edu.upc.talent.swqa.campus.test.utils;

import edu.upc.talent.swqa.campus.domain.User;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Set;

public final class TestFixtures {

  private TestFixtures() {}

  public static final CampusAppState defaultInitialState;
  public static final Group swqa = new Group("1", "swqa");

  public static final User johnDoe = new User(
        "1",
        "John",
        "Doe",
        "john.doe@example.com",
        "student",
        "swqa",
        Instant.now().minus(200, ChronoUnit.DAYS).truncatedTo(ChronoUnit.MILLIS)
  );

  public static final User janeDoe = new User(
        "2",
        "Jane",
        "Doe",
        "jane.doe@example.com",
        "student",
        "swqa",
        Instant.now().minus(100, ChronoUnit.DAYS).truncatedTo(ChronoUnit.MILLIS)
  );

  public static final User mariahHairam = new User(
        "3",
        "Mariah",
        "Harris",
        "mariah.hairam@example.com",
        "teacher",
        "swqa",
        Instant.now().minus(300, ChronoUnit.DAYS).truncatedTo(ChronoUnit.MILLIS)
  );

  static {
    defaultInitialState = new CampusAppState(
          new UsersRepositoryState(
                Set.of(
                      johnDoe,
                      janeDoe,
                      mariahHairam
                ),
                Set.of(swqa)
          ),
          Set.of()
    );
  }


}
