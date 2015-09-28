-- ids to mail 
select id 
 from contact 
 where tenant_id = 'firmgains' and id > 1670 and stage not in( 'deleted','On hold','Cold' ) and do_not_email = false;
 
select concat('sleep 2; curl  -u firmgains:firmgains http://tstephen:5QuiWYYt@api.knowprocess.com:8082/msg/firmgains/firmgains.followUp.json?query=%7B%22contactId%22%3A%22http%3A%2F%2Fapi.knowprocess.com%3A8082%2Fcontacts%2F',id,'%22%2C%22tenantId%22%3A%22firmgains%22%7D&businessDescription=FG+email') 
 from contact 
 where tenant_id = 'firmgains' and id > 1670 and stage not in( 'deleted','On hold','Cold' ) and do_not_email = false
 INTO OUTFILE '/var/tmp/followup.sh'
 
 
-- Find notes that hold a valuation 
select contact_id from note where contact_id in (select id from contact where tenant_id = 'firmgains') and content like '%Valuation performed%';

-- add activity for notes 
 insert into activity (content, occurred, type, contact_id) 
select 'Valuation performed, see notes for details', created, 'valuation', contact_id
from note where contact_id in (select id from contact where tenant_id = 'firmgains') and content like '%Valuation performed%';

-- set enquiry type = valuation where it was missed
update  contact c, note n set enquiry_type = 'Valuation' where enquiry_type is null and tenant_id = 'firmgains' and n.contact_id = c.id; 
-- Finding duplicates
select id, concat(first_name, last_name) as 'name' from contact group by name having count(name) >1;

-- Non duplicates 
select id, concat(first_name, last_name) as 'name' from contact group by name having count(name) =1;

 create table tmp_preserve (`id` bigint(20) NOT NULL, name varchar(255));
insert into tmp_preserve 
select max(id), concat(first_name, last_name) as 'name' from contact group by name having count(name) >1;

insert into tmp_preserve 
select id, concat(first_name, last_name) as 'name' from contact group by name having count(name) =1;
 
-- clean up 
 update contact set stage = 'Enquiry' where stage is null;

 insert into activity (content, last_updated, occurred, type, contact_id) 
select 'Valuation, see notes for details', last_updated, first_contact, 'Valuation', id
from contact where enquiry_type like 'Valuation';

insert into activity (content, last_updated, occurred, type, contact_id) 
select 'Registered account', last_updated, first_contact, 'Registration', id
from contact where enquiry_type like 'Registration';

 
-- logical delete all but most recent duplicate 
update contact set stage = 'deleted' 
where  tenant_id = 'firmgains' and id not in (select id from tmp_preserve) ; 

-- migration 

select value from custom_contact_field where name ='accountName';

  select a.id, a.name, ccf.name
  from account a, custom_contact_field ccf 
  where ccf.name ='accountName',  and ccf.value = a.name


update contact c  set account_id = ( 
  select a.id
  from contact_custom_fields l, custom_contact_field ccf, account a 
  where l.custom_fields_id = ccf.id
  and ccf.name ='accountName',  and ccf.value = a.name
  and l.contact_id = c.id
  group by l.contact_id
);


  select l.contact_id, ccf.value, a.name
  from contact_custom_fields l, custom_contact_field ccf, account a 
  where l.custom_fields_id = ccf.id
  and ccf.name ='accountName',  and ccf.value = a.name
  group by contact_id;

  
  select c.email,c.first_name,c.last_name, f.value
  from contact c, contact_custom_fields l, custom_contact_field f 
  where c.id = l.contact_id 
  and l.custom_fields_id = f.id
  and f.name ='sugarId';
  
  c.email,c.first_name,c.last_name, 
  
  select concat('update wp_usermeta set meta_value = \'http://api.knowprocess.com:8082/contacts/',c.id,'\' where meta_value = \'',f.value,'\';')
  from contact c, contact_custom_fields l, custom_contact_field f 
  where c.id = l.contact_id 
  and l.custom_fields_id = f.id
  and f.name ='sugarId';
  //into outfile '/tmp/migrate-users.sql';
  
  SELECT u.id, meta_value, u.user_login
  FROM wp_usermeta m, wp_users u 
  WHERE m.user_id = u.id
  AND m.meta_key ='user_api_id'
  and u.user_login in ('666@stu.gmail.com','stevesheen1@me.com','martyn.lindsay@lineone.net','matthewdeasy@beanmachines.co.uk','baraneh.masoomi@yahoo.com','catchpol@btinternet.com','the4earls@sky.com','richardjohnson@primepakfoods.co.uk','franceshp@hotmail.co.uk','gomezfinance70@gmail.com','talib_hussain786@yahoo.com','carlob@gmail.com','freya@brightandbeutifulhome.com','mgk@chozen.co.uk','ewilliams@edmundcarr.com','steelwise@talktalk.net','naziakaslam@gmail.com','whowantstorock@hotmail.co.uk','jonlumgair@gmail.com','robeth@outlook.com','laura@chelseacorporate.com','paula.stockton@live.co.uk','theversions@hotmail.com','nadinewyer@yahoo.co.uk','salilsdd1@hotmail.com','jwfurniture@hotmail.com','sanju.jagpal@gmail.com','impy@hotmail.co.uk','thomas@lagoni.co.uk','carolynne@flightpartner.co.uk','yangyong3@hotmail.com','gwebbgpc@aol.com','alex.olivetreeinteriors@gmail.com','g.spencer@europa-industries.co.uk','pubs@live.co.uk','andysheps1@gmail.com','neil@angliaeasyenergy.co.uk','wdj1969@hotmail.co.uk','jason@topconconstruction.co.uk','chandel.anku91@gmail.com','davesmith1919@btinternet.com','fabri.margarita@yahoo.co.uk','sererefishfarm@gmail.com','cazmaden@hotmail.co.uk','farrukhislam@ymail.com','mail@rwallis.com','info@candm-contracts.co.uk','marinaspindola@gmail.com','g_ataylor@yahoo.co.um','bruce.casely@gmail.com','andy.gattward@sjpp.co.uk','akinhunter@hotmail.com','neville@cortel.co.uk','pne2424@hotmail.co.uk','rdenton@direct-staff.co.uk','gemmafullarton@live.co.uk','courtview2@hotmail.co.uk','kate@thewriteimpression.co.uk','rwatkyn@gmail.com','test@hebdenantiques.co.uk','efgmb@j.net','charlgaldo_90@hotmail.com','arekthd@gmail.com','markwhitney3@gmail.com','thelondonginclub@gmail.com','mahtems@gmail.com','stevekemp65@hotmail.com','timothychrleswarren@hotmail.com','kadsjfjkhgj@gmail.com','john@b.com','denstonebartlett@gmail.com','amaiag50@gmail.com','cm@prelectronics.co.uk','andygreen@live.co.uk','pauladair22@gmail.com','ra@anderson-shaw.co.uk','stephen@eastelms.co.uk','myipad@warnerbeach.com','spmitchell50@hotmail.com','mark@mark-ashley.com','barristerhamid@yahoo.ca','ppr1ce@hotmail.co.uk','wickfordian@yahoo.co.uk','misterplumber@sky.com','kashifarzoo@samcarpets.com','mccann950@aol.com','felipetilleria@hotmail.com','annaathellosandhighs@btinternet.com','midlandcitymotors@hotmail.co.uk','darren@greyinteriors.co.uk','bob@builder.com','dave.minshall80@gmail.com','donnahartley45@yahoo.com','ilias.argyropoulos@gmail.com','bathtaxi@gmail.com','nrose@welinlambie.com','rs@nuplansurveys.com','sales@rangeroverbreakers.com','rrr@sad.com','neilfarrell1@yahoo.co.uk','watercoolerwales@aol.com','ff@aol.com','mark_shirtcliffe@hotmail.com','nitin.patel@mehulenterprises.co.uk','lanza.shell@gmail.com','tony@vexus.uk.com','ps@han.com','peterweiss@hotmail.co.uk','mariners@hotmail.co.uk','info@cand-contracts.co.uk','allanmckie@riverfordhomedelivery.co.uk','livehome191@gmail.com','parryberyl@gmail.com','info@newleasegroup.com','peterroberts.42@live.co.uk','sigeng1@gmail.com','claudiu@eurowinsolutions.com','chandel.anku91@gmail.com1','info@boxall-clinch.co.uk','little_loopy2@msn.com','workone3@btinternet.com','info@busybee-cleaning.co.uk','jase.milne@vvla.uk','dvergeyle@hotmail.com','phill1208@gmail.com','michealowen@sky.com','randburgeng@gmail.com','paulconway1977@gmail.com','chris1979jenkins@hotmail.com','ali@subaksigns.co.uk','sarah@farmcareuk.com','georgegregori@hotmail.com','david.ashley@live.com','elaine1925@hotmail.co.uk','calvinauyeung@maysfashiontrend.com','carlb@mcglass.co.uk','nigellangstone@joedeluccis.com','sales@vdirect.co.uk','torbayaccounting@hotmail.co.uk','mamun@londontradition.com','patrick.lenehan123@btinternet.com','jagdeepsanghera@hotmail.co.uk','roy.morey@hotmail.co.uk','clinning@talktalk.net','freya@brightandbeautifulhome.com','rwatkyn@icloud.com','mrwtroup@gmail.com','chris@cndrinks.co.uk','kl@aesukltd.co.uk','craig.denton@streaminc.co.uk','mark@outdoorleisureuk.co.uk','floransb37@yahoo.co.uk','kirtykhemka@Hotmail.com','elbarham@gmail.com','parrydavid68@gmail.com','paul.rees@wmctraining.co.uk','andrew.mazin@am-associates.co.uk','kat.toft@googlemail.com','andy.hunt@bcmscorporate.com','rameshpuri1948@yahoo.co.uk','sukirana@live.com','rfwrfwrt@gmail.com','harringtont@gmail.com','laura@festivalbrides.co.uk','stephenbrannen06@aol.com','peterf@fhpltd.co.uk','patriciab@google.net','farrukhislam@hotmail.com','simonredmond@talktalk.net','sadsacksitting@hotmail.com','rick@rickgoebel.ca','caan@zerodegreeac.com','ravijobs@live.co.uk','iris@karriswelding.co.uk','simon.p.goodwin@gmail.com','leeveeyou@yahoo.com','sales@datacom.co.uk','janeyrmonkey@yahoo.co.uk','anthony.cole@hotmail.co.uk','chris@whiteboxphotography.co.uk','donal65@icloud.com','stevelyons@msospace.com','peterk-1@hotmail.com','mmm@djbhfduf.com','info@lazypig.co.uk','demarpy@yahoo.com','frances@bonusbooks.co.uk','rgoddard@evolutioncbs.co.uk','jason.jones@inner-security.co.uk','peter.hough@linstol.com','dd.tmurray@btinternet.com','asydd@blueyonder.co.uk','everett.colin@btinternet.com','weeble@bt.com','rlennon@direct-staff.co.uk','slaszlo80@hotmai.com','courtney.duncan@btinternet.com','publisher@eureporter.co','westlymedavid@hotmail.co.uk','ggg@lll.com','markwynn06@gmail.com','sarahjdane1@aol.com','rmontenegro11@gmail.com','charley.drew@ntlworld.com','dafyddhughes@ymail.com','chris-buck@hotmail.com','dave.winwood@cantronik.com','keshbo123@hotmail.co.uk','nickwells10@btinternet.com','chandel.anku91@gmail.comaa','audreydotmitchell@hotmail.co.uk','j.anderson957@btinternet.com','fergal@funkyrenewables.co.uk','sandra_ramsay@hotmail.co.uk','gingadmama1@gmail.com','brenthaigh@hotmail.com','info@salisbury-gymnastics.co.uk','john.lumgair@jglearmonth.co.uk','robin.jackson@adr-international.com','pclarney@live.co.uk','jack@cherryduckbistro.co.uk','enquiries@derventiobrewery.co.uk','john@ergodigital.com','rose111907@hotmail.com','brendanohare@killeavey.co.uk','natalie.crayton@yahoo.co.uk','webmaster@experienced-people.net','stj@allenstps.com','peterjcseaton@aol.com','sbourne@email.arizona.edu','colbail@hotmail.com','a5aem@tiscali.co.uk','burrowsjmb@yahoo.co.uk','dave.smith@idnet.com','pvdesprigre@manx.net','terry@danburyrowe.co.uk','derrylagos@yahoo.com','davids@ler.ltd.uk','flutefocus@gmx.co.uk','katie.paine@hotmail.com','sandrobergamini93@icloud.com','partyfriendz@hotmail.co.uk','anamayahealth@outlook.com','gleballs2000@outlook.com','iandorrell@yahoo.co.uk','jf@yahoo.com','kbenn@cryopod.co.uk','mdjcarr@btinternet.com','phillipcord@aol.com','clinton@experienced-people.net','contact@henleypropertymanagement.com','julie@crystaclearaccountancy.co.uk','colinaspinall@blueyonder.co.uk','dirtbusters@talktalk.net','matilde.gemeli@yahoo.com','yu.chen917@gmail.com','j_pehar@hotmail.com','team@gardenatics.co.uk','lassepettersen@hotmail.com','kate.mct@gmail.com','jun7m65@aol.com','adam@freestate.co.uk','simon.goodwin@mechanicalintegration.com','baz@hmgt.co.uk','billyjobobuk@yahoo.co.uk','cdemosthenoua@gmail.com','neildevey@hotmail.com','sean@sean.com','info@relaxspa.co.uk','knellarno.3@hotmail.com','info@virtuoso-group.co.uk','markstirgess@me.com','chonor@me.com','jonathan.russell@live.co.uk','dave.coles@sky.com','coopy@aol.com','seref@zlgroup.co.uk','richard@micro-id.co.uk','mrksolmaz@gmail.com','mike.pedersen@norway2uk.com','rgemjade@aol.com','tmjnorton@aol.com','lesley.mitch@hotmail.co.uk','john@allaboutcheese.co.uk','KKD@GMAIL.COM','christensoncar@aol.com','simon.brooks@seventa.co.uk','1972gm@gmail.com','adam@relaxspa.co.uk','info@about-houses.co.uk','Sameena@businessexchange.ca','ddrecycling@hotmail.co.uk','les.vipond@ev-ent.co.uk','STEPH.JOLIN@BTINTERNET.COM','davidbisset83@hotmail.com','monica_soni@hotmail.com','halil007@hotmail.com','sanjujagpal@gmail.com','steve@akbukresortgroup.com','bob@flightpartner.co.uk','steve.bird@viewpointbusiness.co.uk','mtmiah@btinternet.com','Jay@cjconstructionltd.com','dfgfn@hitmail.com','orionoivan@gmail.com','davidsnash@aol.com','pierre.pw100@gmail.comrealitas','kieranpye@hotmail.com','LEMONFACE314@YAHOO.COM','send_2_louise@yahoo.co.uk','julia@moonlit.eclipse.co.uk','dian.mundell@gmail.com','ryan-jenkins-@hotmail.co.uk','bkjhewson@hotmail.co.uk','richedwards84@gmail.com','will9jo@gmail.com','colin@crdesign.org.uk','bl@bl.com','brit-designs@supanet.com','zaf4r001@aol.com','tjb1989@hotmail.co.uk','pat@google.net','nigel.bond@nigelmarks.co.uk','jan_muddyboots@btinternet.com','sales@multiplemonitors.co.uk','stefan.skrzynski@captiveminds.com','jason.wyles@pbm-ltd.com','martyn.blundell@hotmail.co.uk','prgdeep@hotmail.co.uk','gregthay@hotmail.com','rebeccawalker8@me.com','lee_whiteley@hotmail.co.uk','climbersway@hotmail.co.uk','janet@argante.co.uk','info@ktconnection.co.uk','g.quinton@hotmail.co.uk','kayhemingway@hotmail.com','emilykendall91@icloud.com','emma.smith1@mac.com','dereksat@hotmail.co.uk','armstrong@lineone.net','steve@liquidmotionwatersports.co.uk','salmanross@aol.com','simon@muskermcintyre.co.uk','don@andonfreres.co.uk','Ijones@hotmail.com','pierre.pw100@gmail.com','amirarvin1@yahoo.com','danny_topper@hotmail.co.uk','ian.mckay4@sky.com','Martin@timbermaster.co.uk','cathryn.williams@hotmail.co.uk','floransb@yahoo.co.uk','annhask@hotmail.co.uk','k4u53r@hotmail.com','beckydotveal@polysource.co.uk','icab@peakbp.force9.co.uk','fred@ntlworld.com','flaaash37@yahoo.co.uk','glynroberts20@btinternet.com','norman.mandry@live.co.uk','slaszlo80@hotmail.com','lazster01@hotmail.com','michaelroyales@btinternet.com','aircon4you@yahoo.co.uk','fran@plussdriversuk.com','jonnyjrrussell@gmail.com','Info@harpleyequestrian.co.uk','mike_cunningham_r@yahoo.co.uk','paul.wragby@virgin.net','adam@gaelectrical.co.uk','miketomhill@gmail.com','nrsmith@live.co.uk','joycemeakin@aol.com','moira.clark92@btinternet.com','sue.jack@hotmail.co.uk','jessmet@hotmail.co.uk','dthh@guffg.com','steve@northnottsblinds.co.uk','admin@charliewaite.com','djmpropertydevelopment@live.co.uk','maritz.cloete@csrisk.co.uk','farhadak@hotmail.com','jacqmakeup@gmail.com','riceyt@hotmail.co.uk','jusden@jusden.com','admin@feelinhungry.com','davidrnickson@gmail.com','arundhand@hotmail.com','john_wood82@hotmail.com','tackshopkent@googlemail.com','jib_asl@hotmail.com','sder@gmail.com','andrew@bices.co.uk','Kev947@hotmail.com','ross.meigh@add-marketing.co.uk','Mikekidd@hotmail.co.uk','d.c@msw.com','patricia@google.net','john.fenty@ntlworld.com','p.rajdeep@hotmail.com','sandrobergamini94@icloud.com','gledhillchris@aol.com','chris@acorn-networks.co.uk','klinich3@gmail.com','seanhotdog1987@gmail.com','simonwilliams@drw.co.uk','martin@global-mart.co.uk','abigail.cobblah@boatandcobbles.com','jagpal0singh123@gmail.com','gsaggu@blueyonder.co.uk','np@yahoo.com','noosha32@gmail.com','gsshome@btinternet.com','gregjackson@live.co.uk','john@onlineretailingltd.co.uk','martin@jenkinsbakery.co.uk','info@goalsaccomplished.com','uuuyhnknoj@hotmail.com','sonnetw@yahoo.co.uk','james@mycreditexpert.co.uk','spj99@me.com','ross@hhcbs.co.uk','graham.chapple@rapid-esl.co.uk','albacoaches@hotmail.co.uk','dchome@live.co.uk','mpj33@hotmail.co.uk','aplews@madasafish.com','chenmagere@gmail.com','garrycampbell40@hotmail.co.uk','sredpath@me.com','joshmassey@me.com','thelondongnclbu@gmail.com','anamayahealth@outlook.Comesec','wrappedup@mail.com','irek.kaldowski@gmail.com','lehanne@me.com','mark.whitney@aramar.co.uk','lmo@prelectronics.com','paul.barnes@adminteam.org.uk','practicesheffield@hotmail.com','asd@dfdf.com','fradors98@aol.com','daf_hughes@hotmail.com','phil@bendada.co.uk','rich@studentstorage.com','Tim.Hardman@avondale.co.uk','gjones.ac@gmail.com','paullloyd79@btinternet.com','info@mmmmm.com','icesbo@yahoo.co.uk','davesmithmk@gmail.com','courtneyduncan@candm-contracts.co.uk','classicservices@btinternet.com','info@syncapt.com','barbaradoogan@msn.com','cathsutherland@sky.com','esan_jana@yahoo.co.uk','fred@bedrock.com','ravi@ozerecosolutions.com','nicparkins@aol.com','vickiewaite@talktalk.net','rob@kick-a-bout.com','aa@bb.com','sales@simpleaccounting1980.co.uk','ritahumphries@gmail.com','info@maflooring.co.uk','jack.w@hotmail.com','mike@greeendealms.com','francis.rarity@sky.com','fuadmammadov@mail.ru','edbarnett1@sky.com','diane.gillen@btinternet.com','housemaidsdurham@yahoo.co.uk','info@selectbuildingsolutions.co.uk')