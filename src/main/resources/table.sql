create table edu_score
(
	id varchar default gen_random_uuid() not null
		constraint edu_score_pk
			primary key,
	v_lesson varchar,
	n_score numeric,
	v_name varchar
);

alter table edu_score owner to postgres;

