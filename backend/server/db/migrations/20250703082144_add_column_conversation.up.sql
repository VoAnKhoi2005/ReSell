ALTER TABLE conversations
ADD COLUMN is_selling BOOLEAN DEFAULT FALSE;

ALTER TABLE conversations
ADD COLUMN offer INTEGER NULL