package main.tests;

import main.manager.tasks.memory.InMemoryTaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.io.IOException;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    @BeforeEach
    public void beforeEach() throws IOException, InterruptedException {
        super.beforeEach();
        taskManager = new InMemoryTaskManager();
    }

    @AfterEach
    public void afterEach() {
        super.afterEach();
    }

}