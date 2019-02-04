create table edu_score
(
	id varchar default uuid_generate_v4() not null
		constraint edu_score_pk
			primary key,
	v_lesson varchar,
	n_score numeric,
	v_name varchar
);

alter table edu_score owner to postgres;

