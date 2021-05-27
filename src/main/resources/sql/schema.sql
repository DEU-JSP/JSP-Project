# Trash Generate Query
CREATE TABLE trash AS
SELECT * FROM inbox WHERE 1=2;

# Move Mail Inbox to Trash
INSERT INTO trash SELECT * FROM inbox WHERE inbox.message_name = param1 AND inbox.repository_name = param2;

# Move Mail Inbox to Trash
DELETE FROM trash WHERE message_name = param1 AND repository_name = param2;
