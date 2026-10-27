CREATE TABLE IF NOT EXISTS tasks (
 id UUID PRIMARY KEY,
 user_id UUID NOT NULL,
 title VARCHAR(200) NOT NULL,
 description TEXT,
 status VARCHAR(30) NOT NULL,
 due_date DATE
);
CREATE INDEX IF NOT EXISTS tasks_user_idx ON tasks(user_id);
