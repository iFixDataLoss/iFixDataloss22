import spacy



def analysePhrase(phrase):
    if contain_chinese(phrase):
        return 'invalid'
    verb_flag = False
    noun_flag = False
    sentence = 'I want to ' + phrase
    nlp = spacy.load("/storage/emulated/0/Pictures/en_core_web_sm-2.2.5")
    doc = nlp(sentence)
    for i in range(3,len(doc)):
        pos = doc[i].pos_
        if pos in ['VERB','ADP']:
            verb_flag = True
        if pos in ['NOUN','NUM']:
            noun_flag = True
    if verb_flag and noun_flag:
        return 'verb noun'
    elif verb_flag and (not noun_flag):
        return 'only verb'
    elif (not verb_flag) and noun_flag:
        return 'only noun'
    else:
        return 'invalid'

def contain_chinese(str):
    for ch in str:
        if u'\u4e00' <= ch <= u'\u9fff':
            return True
    return False