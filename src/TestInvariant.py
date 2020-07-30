import re

regex = r"(T0,)(.*?)(((T1,)(.*?)(T3,)(.*?)(((T5,)(.*?)(((T9,)(.*?)(T15,))|((T10,)(.*?)(T16,))))|((T13,)(.*?)(T7,))(.*?)(((T9,)(.*?)(T15,))|((T10,)(.*?)(T16,)))))|()(T2,)(.*?)(T4,)(.*?)(((T6,)(.*?)(((T11,)(.*?)(T15,))|((T12,)(.*?)(T16,))))|((T14,)(.*?)(T8,)(.*?)(((T11,)(.*?)(T15,))|((T12,)(.*?)(T16,))))))"

#outputSequence = "[T0, T1, T3, T0, T1, T0, T1, T0, T2, T0, T1, T0, T2, T0, T2, T0, T2, T5, T9, T3, T15, T0, T1, T13, T7, T9, T3, T15, T0, T1, T5, T10, T4, T16, T0, T1, T6, T3, T11, T15, T0, T2, T13, T7, T4, T10, T16, T0, T1, T6, T12, T3, T16, T0, T2, T13, T7, T9, T4, T15, T0, T1, T14, T8, T12, T4, T16, T0, T2, T6, T3, T11, T15, T0, T2, T13, T7, T10, T3, T16, T0, T1, T5, T9, T4, T15, T0, T1, T14, T8, T3, T12, T16, T0, T2, T13, T7, T4, T9, T15, T0, T1, T6, T11, T3, T15, T0, T2, T5, T4, T10, T16, T0, T1, T14, T8, T3, T11, T15, T0, T2, T13, T7, T10, T3, T16, T0, T1, T5, T9, T4, T15, T0, T1, T6, T3, T11, T15, T0, T2, T13, T7, ]"
outputSequence = "[T0, T1, T3, T0, T2, T0, T1, T0, T1, T0, T2, T0, T2, T0, T1, T0, T2, T5, T4, T9, T15, T0, T1, T6, T12, T3, T16, T0, T2, T13, T7, T4, T10, T16, T0, T1, T14, T8, T3, T11, T15, T0, T2, T5, ]"

matches = re.finditer(regex, outputSequence, re.MULTILINE)

leftTransitions = outputSequence

for matchNum, match in enumerate(matches, start=1):
    
    print ("Match {matchNum} was found at {start}-{end}: {match}".format(matchNum = matchNum, start = match.start(), end = match.end(), match = match.group()))
    
    for groupNum in range(0, len(match.groups())):
        groupNum = groupNum + 1
        
        print ("Group {groupNum} found at {start}-{end}: {group}".format(groupNum = groupNum, start = match.start(groupNum), end = match.end(groupNum), group = match.group(groupNum)))
        if((match.group(groupNum) != None) and match.group(groupNum)[0] != " " and (len(match.group(groupNum))>2 and len(match.group(groupNum))<=4)):
            print ("Grupo Válido: {groupNum} found at {start}-{end}: {group}".format(groupNum = groupNum, start = match.start(groupNum), end = match.end(groupNum), group = match.group(groupNum)))
            print("Se reemplazará" + match.group(groupNum) + "por un espacio en blanco.")
            leftTransitions = leftTransitions.replace(match.group(groupNum), "", 1)

print(leftTransitions)

