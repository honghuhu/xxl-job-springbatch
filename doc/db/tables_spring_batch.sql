CREATE database if NOT EXISTS `xxl_job` default character set utf8mb4 collate utf8mb4_unicode_ci;
use `xxl_job`;

SET NAMES utf8mb4;

# drop table if exists batch_job_execution_context;
# drop table if exists batch_job_execution_params;
# drop table if exists batch_job_execution_seq;
# drop table if exists batch_step_execution_context;
# drop table if exists batch_step_execution_seq;
# drop table if exists batch_job_seq;
# drop table if exists batch_step_execution;
# drop table if exists batch_job_execution;
# drop table if exists batch_job_instance;

create table batch_job_execution_seq
(
    id         bigint not null,
    unique_key char   not null,
    constraint unique_key_un
        unique (unique_key)
)
    collate = utf8_bin;

create table batch_job_instance
(
    job_instance_id bigint       not null
        primary key,
    version         bigint       null,
    job_name        varchar(100) not null,
    job_key         varchar(32)  not null,
    constraint job_inst_un
        unique (job_name, job_key)
)
    collate = utf8_bin;

create table batch_job_execution
(
    job_execution_id           bigint        not null
        primary key,
    version                    bigint        null,
    job_instance_id            bigint        not null,
    create_time                datetime      not null,
    start_time                 datetime      null,
    end_time                   datetime      null,
    status                     varchar(10)   null,
    exit_code                  varchar(2500) null,
    exit_message               varchar(2500) null,
    last_updated               datetime      null,
    job_configuration_location varchar(2500) null,
    constraint job_inst_exec_fk
        foreign key (job_instance_id) references batch_job_instance (job_instance_id)
)
    collate = utf8_bin;

create table batch_job_execution_context
(
    job_execution_id   bigint        not null
        primary key,
    short_context      varchar(2500) not null,
    serialized_context text          null,
    constraint job_exec_ctx_fk
        foreign key (job_execution_id) references batch_job_execution (job_execution_id)
)
    collate = utf8_bin;

create table batch_job_execution_params
(
    job_execution_id bigint       not null,
    type_cd          varchar(6)   not null,
    key_name         varchar(100) not null,
    string_val       varchar(250) null,
    date_val         datetime     null,
    long_val         bigint       null,
    double_val       double       null,
    identifying      char         not null,
    constraint job_exec_params_fk
        foreign key (job_execution_id) references batch_job_execution (job_execution_id)
)
    collate = utf8_bin;

create table batch_job_seq
(
    id         bigint not null,
    unique_key char   not null,
    constraint unique_key_un
        unique (unique_key)
)
    collate = utf8_bin;

create table batch_step_execution
(
    step_execution_id  bigint        not null
        primary key,
    version            bigint        not null,
    step_name          varchar(100)  not null,
    job_execution_id   bigint        not null,
    start_time         datetime      not null,
    end_time           datetime      null,
    status             varchar(10)   null,
    commit_count       bigint        null,
    read_count         bigint        null,
    filter_count       bigint        null,
    write_count        bigint        null,
    read_skip_count    bigint        null,
    write_skip_count   bigint        null,
    process_skip_count bigint        null,
    rollback_count     bigint        null,
    exit_code          varchar(2500) null,
    exit_message       varchar(2500) null,
    last_updated       datetime      null,
    constraint job_exec_step_fk
        foreign key (job_execution_id) references batch_job_execution (job_execution_id)
)
    collate = utf8_bin;

create table batch_step_execution_context
(
    step_execution_id  bigint auto_increment not null
        primary key,
    short_context      varchar(2500)         not null,
    serialized_context text                  null,
    constraint step_exec_ctx_fk
        foreign key (step_execution_id) references batch_step_execution (step_execution_id)
)
    collate = utf8_bin;

create table batch_step_execution_seq
(
    id         bigint not null,
    unique_key char   not null,
    constraint unique_key_un
        unique (unique_key)
)
    collate = utf8_bin;

### 初始化序列表
insert into batch_step_execution_seq (id, unique_key)
select *
from (select 0 as id, '0' as unique_key) as tmp
where not exists(select * from batch_step_execution_seq);

insert into batch_job_execution_seq (id, unique_key)
select *
from (select 0 as id, '0' as unique_key) as tmp
where not exists(select *from batch_job_execution_seq);

insert into batch_job_seq (id, unique_key)
select *
from (select 0 as id, '0' as unique_key) as tmp
where not exists(select * from batch_job_seq);

# --------------example table---------------
CREATE TABLE people
(
    person_id  BIGINT NOT NULL PRIMARY KEY,
    first_name VARCHAR(20),
    last_name  VARCHAR(20)
);