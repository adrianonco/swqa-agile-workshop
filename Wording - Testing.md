# SWQA: Agile testing workshop

## Questions

1. For each one of the following classes, pick an example of a test where the pattern "Arrange-Act-Assert" or "AAA with state" are applied and identify clearly the three parts of the pattern.
    - `CampusAppTest`
    - `CampusAppMockedTest`
    - `CampusAppEndToEndTest`
    - `UsersRepositoryTest`
2. Briefly describe the main issue(s) with `CampusAppEndToEndTest`. How would you fix them?
3. Briefly describe the Pros and Cons of the strategy applied in `CampusAppEndToEndTest`, assuming that the issue or issues described in the previous question are fixed.
4. Briefly describe the Pros and Cons of the strategy applied in `CampusAppMockedTest`.
5. Briefly describe the Pros and Cons of the strategy applied in `CampusAppTest`.
6. For each one of the following patterns, enumerate the test classes where it is applied.
    - AAA with state
    - Isolated production-like external systems
    - Production-ready in-memory test doubles
7. What is an example of a production-ready in-memory test fake in this project? Why do we say it is production-ready?
8. Could a buggy test fake make `CampusAppTest` pass even if CampusApp had bugs? If so, what can be done?
9. Remove the `@Disabled` annotation from `UsersRepositoryTest::testCreateUserFailsIfGroupDoesNotExist` and run the tests. What happens? Why? How is this related to the previous question? Note: Add the `@Disabled` annotation again before moving on to the next section.
10. Remove the `@Disabled` annotation from `UsersRepositoryTest::testCreateUser` and run the tests. What happens? Why? What is causing the problem? How could it be fixed? Note: Add the `@Disabled` annotation again before moving on to the next section. 
11. Which methods in UsersRepository are not tested?

## Tasks

- [ ] Write a test for `CampusApp::createUser` in `CampusAppTest`
- [ ] Write a test for `CampusApp::createUser` in `CampusAppMockedTest`
- [ ] Write a test for `CampusApp::createUser` in `CampusAppEndToEndTest` in a way that solves the issue described in question 1. Pay attention to the strategy and patterns you use.
- [ ] Fix the bug causing `UsersRepositoryTest::testCreateUserFailsIfGroupDoesNotExist` to fail
- [ ] Write tests for the **one of** the untested methods in UsersRepository (those you identified in question 10)

## Answers

*By Adrián Orduna*

### Question 1

`CampusAppTest`

```java
@Test  
public void testCreateGroup() {  
  //  Assert  
  final var app = getApp(defaultInitialState);  
  final var newGroup = new Group("0", "bigdata");  
  // Act  
  app.createGroup(newGroup.id(),newGroup.name());  
  // Assert  
  final var expectedFinalState = new CampusAppState(  
        new UsersRepositoryState(  
              defaultInitialState.usersRepositoryState().users(),  
              union(defaultInitialState.usersRepositoryState().groups(), Set.of(newGroup))  
        ),  
        Set.of()  
  );  
  assertEquals(expectedFinalState, getFinalState());  
}
```

`CampusAppMockedTest`

```java
@Test  
public void testCreateGroup() {  
  // Arrange is covered by the setup method 
  // Act  
  app.createGroup("1", "bigdata");  
  // Assert  
  verify(usersRepository).createGroup(any(), eq("bigdata"));  
  verifyNoMoreInteractions(usersRepository);  
  verifyNoMoreInteractions(emailService);  
}
```

`CampusAppEndToEndTest`

```java
@Test  
public void testCreateGroup() {  
  // Arrange is covered by the setUpDatabaseSchema method  
  // Act 
  app.createGroup("2", "bigdata");  
  //  Assert is missing  
}
```

`UsersRepositoryTest`

```java
@Test  
default void testGetUsersByGroup() {  
  // Arrange  
  final var repository = getRepository(defaultInitialState);  
  // Act  
  final var actual = repository.getUsersByGroup("swqa");  
  // Assert  
  assertEquals(defaultInitialState.users(), new HashSet<>(actual));  
  assertExpectedFinalState(defaultInitialState);  
}
```

### Question 2

The `CampusAppEndToEndTest` class has several end-to-end (E2E) tests defined for a `CampusApp` application. However, the tests currently execute actions but do not contain any assertions to verify the outcomes of those actions. Without assertions, a test cannot determine if the application behaves as expected.

An example of adding assertions to `testCreateGroup`in order to fix the issue might be:

```java  
@Test  
public void testCreateGroup() {  
  // Arrange  
  final String groupId = "2";  
  final String groupName = "bigdata";  
  
  // Act  
  app.createGroup(groupId, groupName);  
  
  // Assert  
  final UsersRepositoryState finalState = PostgreSqlUsersRepositoryTestHelper.getFinalState(db);  
  boolean groupExists = finalState.groups().stream()  
          .anyMatch(group -> group.id().equals(groupId) && group.name().equals(groupName));  
  
  Assertions.assertTrue(groupExists, "Group was not created as expected");  
}
```

### Question 3

The `CampusAppEndToEndTest` test class is using an end-to-end testing strategy, which allows implement AAA with state and Isolated, production-like external services.

Pros:

1. **Production-like Environment Testing**: End-to-end strategy allows interact with the application as a whole, so implementing isolated, production-like external services means the tests are close to how real user would interact with the system.
2. **Components' Assembly and Dynamic Verification**: This strategy allows verifying the interaction between different components of the application and their assemblage considering their several states.
3. **User Flow Simulation**: E2E tests can simulate common user behaviors and business workflows allowing the detection of system issues furthermore unit and integration testing.

Cons:

1. **Complexity, Costs and Maintenance**: E2E strategy are complex to write and maintain because they require the setup of the production-like application, which also is translated into higher costs, more time of implementation and resource intensiveness.
2. **Slower Execution**: As E2E tests interact with the isolated, production-like external services and databases, their execution is slower than others like unit or integration tests, and may cause flakiness.

### Question 4

The strategy used in the `CampusAppMockedTest`class is based on using test doubles as mocks for testing the behavior of the dependencies `EmailService` and `UserRepository`following the Test Driver Adapters strategy.

Pros:

1. **Simplification and Replacing of Production Elements**: Using test doubles as mocks simplify the setup by replacing complex dependencies or external systems through simulated pre-programmed components which call what they are expecting to receive without manipulating production elements.
2. **Behavior Verification**: As this strategy acts as a driver simulating the behavior of the system after receiving the calls of the dependencies mocked (Driver ports), the fake hexagon implementation allows verifying the correct behavior of `CampusApp`without having to interact will the actual infrastructure components.

Cons:

1. **Fake Behavior**: As the tests are pre-programed based on the fulfillment of some expectations, they don't really act they would act in real situations. This means the mocks may not accurately reflect the real system's behavior.
2. **False Confidence**: Passing tests with this strategy may give false positives as it simulates the behavior of real ports with the system.

### Question 5

The strategy applied in `CampusAppTest`uses in-memory production-ready test fakes as it involves substituting actual dependencies with in-memory versions (`InMemoryUsersRepository`and `InMemoryEmailService`) that are intended to behave like their production counterparts (real database operations and sending emails).

Pros:

1. **Speed**: With this strategy, tests run quickly because interact with in-memory components instead of external systems. This allows a more focused testing as the domain logic is tested without the complexity of real external services.
2. Deterministic: As these tests are isolated from external factors that could introduce flakiness, this strategy enhances their reliability and simplification.

Cons:

1. **Fake Behavior**: As in-memory fakes may not perfectly emulate the behavior of the production elements, they can lead to passing tests that would fail in real world scenarios.
2. **Implementation Costs**: The significant complexity and growth of the development and maintenance of in-memory fakes can be translated into potential overhead resources and costs.

### Question 6

AAA with state
+ `CampusAppTest`
+ `CampusAppClassicEndtoEndTest`
+ `AssertsTest`
+ `UtilsTest`

Isolated production-like external systems
+ `CampusAppEndToEndTest`

Production-ready in-memory test doubles
+ `CampusAppTest`
+ `CampusAppClassicEndtoEndTest`

### Question 7

The test class `CampusAppTest` is an example of a production-ready in-memory test fake because its components `InMemoryUserRepository`and `InMemoryEmailService` act as simplified versions of their production counterparts, simulating their behavior entirely in memory.

When we say that in-memory test fakes are "production-ready" we claim that the in-memory test fakes implement the same interfaces and behavior as their production counterparts. So the interaction of the application with these components through the ports can be accurately simulated in tests independently of its production adapters even though it closely mirrors production behavior.

### Question 8

Yes, a buggy test fake could indeed cause `CampusAppTest`to pass even if `CampusApp`contained bugs if the test fakes (`InMemoryUsersRepository`and `InMemoryEmailService`) do not accurately simulate the behavior of the real components they replace.

To mitigate the risks of flaky or false positives in test fakes, we have two main alternatives:
+ Test in integration with Isolated, production(-like) external systems that mimic the production environment allow catch integration issues in a controlled environment without exposing data.
+ Use Mocks and Stubs to simulate interactions with dependencies instead of in-memory fakes allow verifying interactions with dependencies in a faster and more controlled way.

### Question 9

After removing @Disabled, `InMemoryUsersRepositoryTest` expects a `RuntimeException`when trying to create a user with a non-existent group, but no exception is thrown, and `PostgreSqlUsersRepository` expects a `RuntimeException` but fail due to a constraint violation.

This is related with the previous question highlighting potential discrepancies between in-memory fakes and actual database-backed repositories.

The situation underscores the need of having a balanced testing strategy that includes both isolated tests with fakes/mocks as well as integration tests, in order to develop consistent implementations across different versions of a component (in-memory vs. production) following the hexagonal architecture approach.

### Question 10

When you run the test after removing @Disabled, a slight different in the execution time between when the `User`object is created and when it's compared against the expected value occurs. The failure messages indicate that the test expects a `User`object with a specific `createdAt`timestamp, but the test has a slightly different timestamp.

This is caused because the custom `assertEquals`method does not account for the variability in `createdAt`timestamps. It expects an exact match, but this is unrealistic due the delayed nature of system processing variable times.

In order to mitigate this situation, one possible strategy is adopting Test driver adapters through mocking as it allows for precise control over external factors behavior like time. By mocking the system clock or the method that generates the `createdAt`timestamp, you can control its behavior and its output to ensure consistency between the arranged expected state and the actual state produced by the test.

### Question 11

The method in `UsersRepository`that hasn't been tested is `getUsersInBirthday`. 

