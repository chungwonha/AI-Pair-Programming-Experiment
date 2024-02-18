

UPDATE public.wsj_central_banking_article_entity
SET published_date = REPLACE(published_date, 'Updated ', '')
WHERE published_date LIKE 'Updated %';

select *
from public.wsj_central_banking_article_entity;
where published_date ='' or published_date is null;

delete from public.wsj_consumer_article_entity
 where article_text is null;
where published_date ='' or published_date is null;

select *
from public.wsj_consumer_article_entity;
where published_date ='' or published_date is null;

--99f28051

UPDATE public.washington_post_economy_article_entity
SET published_date = TO_CHAR(TO_TIMESTAMP(published_date, 'Month DD, YYYY at HH:MI a.m. "EST"'), 'Mon-DD-YYYY');

WHERE your_date_column IS NOT NULL;


select *
from public.washington_post_economy_article_entity;

select *
from public.wsj_consumer_article_entity;

select *
from public.wsj_earnings_article_entity;

select *
from public.wsj_global_economy_article_entity;
where published_date is null or published_date ='';

select *
from public.wsj_housing_article_entity
where published_date is null or published_date ='';

delete from public.wsj_housing_article_entity
where published_date is null or published_date = '';	

select *
from public.wsj_job_article_entity;
where published_date is null or published_date ='';

select *
from public.wsj_trade_article_entity;

delete from public.wsj_trade_article_entity
where published_date is null or published_date = '';	
