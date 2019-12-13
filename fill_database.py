import json
import jsonpickle as jp
import re
import firebase_admin
from firebase_admin import credentials
from firebase_admin import db

class Question:

    def __init__(self, q_num, q_text, answer_type, answers, next_q, next_sub_q, survey_num, tot_q_num):
        self.questionNum = tot_q_num
        self.questionText = q_text
        self.questionType = answer_type
        self.answer = answers
        self.next_q = next_q
        self.next_sub_q = next_sub_q
        self.survey_num = survey_num
        self.questionId = str(survey_num) + ":" + str(q_num)

    def to_string(self):
        return str(self.q_num) + " " + self.q_text + " " + self.answer_type + " " + self.answers + "\n"

    def to_json(self):
        #return "{\"qId\": \"{}\", \"questionText\" : \"{}\", \"questionType\": \"{}\", \
         #            \"answer\": \"{}\", \"skipLogic\": \"{}\", \"surveyNumber\": \"{}\"\}" \
          #           .format(str(self.questionNum), self.questionText, self.questionType, self.answer, self.skip_logic, str(self.survey_num))
       return "{\n\"qId\": " + str(self.questionId) + ",\n\"qNum\": " + str(self.questionNum) + ",\n\"questionText\": \"" + self.questionText + "\",\n\"questionType\": \"" + self.questionType \
           + "\",\n\"answer\": \"" + self.answer + "\",\n\"surveyNumber\": " + str(self.survey_num) + "\",\n\"nextQuestion\": \""+ self.next_q + "\",\n\"nextSubQuestion\": \"" + str(self.next_sub_q) + "\"\n},\n"

class Survey:
    questions = []

    def __init__(self, id, title):
        self.id = id
        self.title = title

    def add_question(self, question):
        self.questions.append(question)

    def print_questions(self):
        print("Survey %d. %s\n", self.id, self.title)
        for q in self.questions:
            print(q.to_string())

def build_json_obj(q):
    return ({'qNum': str(q.questionNum), "questionText": str(q.questionText), \
         "questionType": str(q.questionType), "answer": str(q.answer), \
             "surveyNumber": str(q.survey_num), "nextQuestion": str(q.next_q), \
                 "nextSubQuestion": str(q.next_sub_q)})



# Firebase credentials & SDK setup
cred = credentials.Certificate("path_to_firebase_cert")
firebase_admin.initialize_app(cred, {
    'databaseURL': 'https://blockassessmentsurvey.firebaseio.com/'
})

# As an admin, the app has access to read and write all data, regradless of Security Rules
ref = db.reference('Questions_Test_Run')
print(ref.get())

# Open questions file
q_file = open('path_to_sample_questions')
flag = 0
counter = 0
survey_num = 0
tot_q_num = 1
surveys = []
questions = []
    
tables = re.split(r"Table \d\.", q_file.read())

# Get rid of first four tables (0a, 0b, 0c and Exit Menu)
for i in range(0, 3):
    tables.pop(0)
# Capture title then get rid of first three lines
for table in tables:
    curr_id = 1
    q_list = table.split('\n')
    title = q_list[0]
    for i in range(0, 3):
        q_list.pop(0)

    # Create new survey
    s = Survey(curr_id, title)
    survey_num += 1

    # Split questions by comma (max set to three splits so if comma in skip logic it shouldn't cause an issue)
    # Create new question object and add it to survey's list of questions
    q_num = 1
    for q in q_list:
        q_parts = re.split('\t', q, 5)
        # Need to check if skip logic is present
        if len(q_parts) > 3 and q_parts[0] and q_parts[5]:
            q = Question(q_num, q_parts[0], q_parts[1], q_parts[2], q_parts[4], q_parts[5], survey_num, tot_q_num)
            questions.append(q)
            q_ref = ref.child(q.questionId).set(build_json_obj(q))
            q_num += 1
            tot_q_num += 1
        elif len(q_parts) > 3 and q_parts[0]:
            q = Question(q_num, q_parts[0], q_parts[1], q_parts[2], q_parts[4], "", survey_num, tot_q_num)
            questions.append(q)
            q_ref = ref.child(q.questionId).set(build_json_obj(q))
            q_num += 1
            tot_q_num += 1

    # Add survey into list of surveys
    surveys.append(s)
