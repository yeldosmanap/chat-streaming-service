-- Add a column to store the user_id in the messages table
ALTER TABLE messages
    ADD COLUMN user_id INT;

-- Add a foreign key constraint to link messages to users
ALTER TABLE messages
    ADD CONSTRAINT fk_messages_users
        FOREIGN KEY (user_id)
            REFERENCES users (id);
