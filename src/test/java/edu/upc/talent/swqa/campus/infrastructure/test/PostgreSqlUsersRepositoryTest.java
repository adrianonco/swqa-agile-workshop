package edu.upc.talent.swqa.campus.infrastructure.test;

import edu.upc.talent.swqa.campus.domain.User;
import edu.upc.talent.swqa.campus.domain.UsersRepository;
import edu.upc.talent.swqa.campus.infrastructure.PostgreSqlUsersRepository;
import edu.upc.talent.swqa.campus.test.utils.Group;
import edu.upc.talent.swqa.campus.test.utils.UsersRepositoryState;
import edu.upc.talent.swqa.jdbc.test.utils.DatabaseBackedTest;
import org.jetbrains.annotations.NotNull;

public final class PostgreSqlUsersRepositoryTest extends DatabaseBackedTest implements UsersRepositoryTest {

  @Override public UsersRepository getRepository(final UsersRepositoryState initialState) {
    PostgreSqlUsersRepositoryTestHelper.setInitialState(db, initialState);
    return new PostgreSqlUsersRepository(db);
  }

  @Override
  @NotNull
  public UsersRepositoryState getFinalState() {
    return new UsersRepositoryState(
          db.selectToSet(
                """
                select u.id, u.name, u.surname, u.email, u.role, g.name, u.created_at
                from users u join groups g on u.group_id = g.id""",
                (rs) -> new User(
                      rs.getString(1),
                      rs.getString(2),
                      rs.getString(3),
                      rs.getString(4),
                      rs.getString(5),
                      rs.getString(6),
                      rs.getInstant(7)
                )
          ),
          db.selectToSet(
                "SELECT id, name FROM groups",
                (rs) -> new Group(rs.getString(1), rs.getString(2))
          )
    );
  }

}