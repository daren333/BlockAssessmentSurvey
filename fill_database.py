import csv
import re

class Question:

    def __init__(self, q_num, q_text, answer_type, answers, skip_logic):
        self.q_num = q_num
        self.q_text = q_text
        self.answer_type = answer_type
        self.answers = answers
        self.skip_logic = skip_logic

    def to_string(self):
        return self.q_text + " " + self.answer_type + " " + self.answers + " " + self.skip_logic + "\n"

class Survey:
    questions = []

    def __init__(self, id):
        self.id = id

    def add(self, question):
        self.questions.append(question)

def is_header(line):
    if line[0] == "Question" and line[1] == "Answer type" and line[2] == "Answers" and line[3] == "Skip logic":
        return True
    else:
        return False

csv_file = open('/Users/dansher/Documents/UMD/courses/fall 2019/Handheld Programming/BlockAssessmentSurvey/questions_list.csv')
flag = 0
counter = 0
survey_num = 0
surveys = []
    
q_header = ["Question", "Answer type", "Answers", "Skip logic"]
csv_reader = csv.reader(csv_file, delimiter=',')
for line in csv_reader:
    if re.search("Table \d", str(line)):
        survey_num += 1
        counter = 1
        s = Survey(survey_num)
        surveys.append(s)
    elif survey_num > 0 and line != q_header and line:
        q = Question(counter, line[0], line[1], line[2], line[3])
        s.add(q)
        counter += 1


f = open("qs_test.txt","w") 

for s in surveys:   
    f.write("Survey # %d\n" % s.id)
    for q in s.questions:
        f.write(q.to_string())