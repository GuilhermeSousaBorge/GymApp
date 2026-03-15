
ALTER TABLE training_programs RENAME COLUMN order_in_program TO program_order;

ALTER TABLE training_sheets RENAME COLUMN order_in_sheet TO order_in_program;

ALTER TABLE training_exercises RENAME COLUMN order_in_training TO order_in_sheet;