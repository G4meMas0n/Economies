#
#
#

INSERT INTO accounts (id, uuid_most, uuid_least, identifier)
VALUES (1, 0, 0, 'Bank') ON DUPLICATE KEY UPDATE id = id;

INSERT INTO versions (plugin_name, version_major, version_minor)
VALUES ('Economies', 1, 0) ON DUPLICATE KEY UPDATE plugin_name = plugin_name;