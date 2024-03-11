package edu.upc.talent.swqa.campus.e2e;

import edu.upc.talent.swqa.campus.domain.CampusApp;
import edu.upc.talent.swqa.campus.infrastructure.PostgreSqlUsersRepository;
import edu.upc.talent.swqa.campus.infrastructure.SmtpEmailService;
import edu.upc.talent.swqa.campus.infrastructure.test.PostgreSqlUsersRepositoryTestHelper;
import static edu.upc.talent.swqa.campus.infrastructure.test.UsersRepositoryTest.defaultInitialState;
import static edu.upc.talent.swqa.campus.test.utils.TestFixtures.swqa;
import edu.upc.talent.swqa.jdbc.test.utils.DatabaseBackedTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public final class CampusAppEndToEndTest extends DatabaseBackedTest {

  private CampusApp app;

  @BeforeEach
  public void setUpDatabaseSchema() {
    PostgreSqlUsersRepositoryTestHelper.setInitialState(db, defaultInitialState);
    final var repo = new PostgreSqlUsersRepository(db);
    this.app = new CampusApp(repo, new SmtpEmailService());
  }

  @Test
  public void testCreateGroup() {
    // Arrange is covered by the setUpDatabaseSchema method
    // Act
    app.createGroup("2", "bigdata");
    //  Assert is missing
  }

  @Test
  public void testSendEmailToGroup() {
    app.sendMailToGroup(swqa.name(), "New campus!", "Hello everyone! We just created a new virtual campus!");
  }

  @Test
  public void testSendEmailToGroupRole() {
    app.sendMailToGroupRole(swqa.name(), "teacher", "Hey! Teacher!", "Let them students alone!!");
  }

}
