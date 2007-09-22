-- I got this from http://www.orafaq.com/faqxml.htm - RMM

create table XMLTable (doc_id number, xml_data XMLType);

insert into XMLTable values (1,
        XMLType('<FAQ-LIST>
           <QUESTION>
                <QUERY>Question 1</QUERY>
                <RESPONSE>Answer goes here.</RESPONSE>
           </QUESTION>
        </FAQ-LIST>'));

select extractValue(xml_data, '/FAQ-LIST/QUESTION/RESPONSE')  -- XPath expression
from   XMLTable
where  existsNode(xml_data, '/FAQ-LIST/QUESTION[QUERY="Question 1"]') = 1;

--yeilds Unknown(2,007)
SELECT * FROM XMLTABLE; 

-- yields the string-ified version
select x.xml_data.getClobVal() from xmltable x

-- yields the string-ified version
select XMLSERIALIZE(CONTENT xml_data)
from   XMLTable