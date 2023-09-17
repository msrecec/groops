CREATE TABLE "notification" (
    "id" SERIAL,
    "user_id" INTEGER NOT NULL,
    "message" TEXT NOT NULL,
    "entity_id" INTEGER NOT NULL,
    "related_entity_id" INTEGER,
    "entity_type" TEXT NOT NULL,
    "read" BOOLEAN NOT NULL DEFAULT FALSE,
    "created_by" CHARACTER VARYING(255) NOT NULL,
    "modified_by" CHARACTER VARYING(255),
    "created_ts" timestamp(0) with time zone NOT NULL,
    "modified_ts" timestamp(0) WITH TIME ZONE,
    CONSTRAINT notification_pkey PRIMARY KEY(id),
    FOREIGN KEY (user_id) REFERENCES "user"(id) ON DELETE CASCADE
);

CREATE INDEX notification_user_idx ON "notification"("user_id");