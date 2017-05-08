import easygui as gui
import sys
import rdflib
import urllib.parse
from rdflib import Graph

title = "Top100"

def selectSearch():
    msg = "What kind of search do you want to perform?"
    choices = ["By song","By artist","By date"]
    return gui.buttonbox(msg, choices=choices)

def songChoiceBox(g):
    songs = getAllSongs(g)
    msg ="What song do you want to see?"
    choice = gui.choicebox(msg, title, songs)

    if choice != None:
        song = retrieveSongByName(g, choice)
        msg = "Song: "+ choice + "\n"
        msg += "Artist: "+urllib.parse.unquote(song["artist"]).split("#")[1].replace("+", " ") + "\n"
        msg += "First entry: "+song["date"].split("T")[0] + "\n"
        msg += "Chord change: "+song["cc"] + "\n"
        msg += "Timbre change: "+song["tc"] + "\n"
        gui.msgbox(msg)


    return choice

def artistChoiceBox(g):
    artists = getAllArtists(g)
    msg ="What artist do you want to see?"

    artist = [a.toPython().encode('ascii', 'ignore') for a in artists]
    artist = [a.toPython().encode('utf-8', 'ignore') for a in artists]
    choice = gui.choicebox(msg, title, artists)

    if choice != None:
        songs = retrieveSongByArtist(g, "http://top100.com/data#"+urllib.parse.quote(choice).replace("%20", "+"))
        if len(songs) == 1:
            choice = songs[0]
        else:
            msg ="What song do you want to see?"
            choice = gui.choicebox(msg, title, songs)

        if choice != None:
            song = retrieveSongByName(g, choice)
            msg = "Song: "+ choice + "\n"
            msg += "Artist: "+urllib.parse.unquote(song["artist"]).split("#")[1].replace("+", " ") + "\n"
            msg += "First entry: "+song["date"].split("T")[0] + "\n"
            msg += "Chord change: "+song["cc"] + "\n"
            msg += "Timbre change: "+song["tc"] + "\n"
            gui.msgbox(msg)

    return choice

def dateInputBox(g):
    msg = "The data are available from 1960-01-02 to 2009-12-26"
    fieldNames = ["Starting year", "Starting month", "Starting day", "Ending year", "Ending month", "Ending day"]
    fieldValues = gui.multenterbox(msg, title, fieldNames)

    if fieldValues is not None:
        start =  [ fieldValues[i] for i in range(0,3) ]
        end =  [ fieldValues[i] for i in range(3,6) ]

        if validateDate(start) and validateDate(end):
            songs = getAllSongsInDate(g, start, end)
            if len(songs) == 0:
                msg ="No songs available"
                gui.msgbox(msg)
            else:
                msg ="What song do you want to see?"
                choice = gui.choicebox(msg, title, songs)

                if choice != None:
                    song = retrieveSongByName(g, choice.split(" **")[0])
                    msg = "Song: "+ choice.split(" **")[0] + "\n"
                    msg += "Artist: "+urllib.parse.unquote(song["artist"]).split("#")[1].replace("+", " ") + "\n"
                    msg += "First entry: "+song["date"].split("T")[0] + "\n"
                    msg += "Chord change: "+song["cc"] + "\n"
                    msg += "Timbre change: "+song["tc"] + "\n"
                    gui.msgbox(msg)

def retrieveSongByArtist(g, artist):
    query = """
    PREFIX mo:  <http://purl.org/ontology/mo/>
    PREFIX rdfs:  <http://www.w3.org/2000/01/rdf-schema#>
    PREFIX foaf:  <http://xmlns.com/foaf/0.1/>

    SELECT ?name WHERE {
        ?song a mo:Track .
        ?song rdfs:label ?name.
        ?song foaf:maker ?artistUrl .

        FILTER (str(?artistUrl)="%s"^^xsd:string)
    }

    """% (artist)

    res = list()
    for row in g.query(query):
        res.append(row[0])

    return res

def retrieveSongByName(g, name):
    query = """
    PREFIX mo:  <http://purl.org/ontology/mo/>
    PREFIX rdfs:  <http://www.w3.org/2000/01/rdf-schema#>
    PREFIX time:  <http://www.w3.org/2006/time#>
    PREFIX foaf:  <http://xmlns.com/foaf/0.1/>
    PREFIX rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#>

    SELECT DISTINCT ?artistUrl ?date ?cc ?tc  WHERE {
        ?song a mo:Track .
        ?song rdfs:label  "%s".
        ?song time:inXSDDate ?date.
        ?song :ChordChange ?cc .
        ?song :TimbreChange ?tc .
        ?song foaf:maker ?artistUrl .
        ?artist a mo:MusicArtist .
    }
    """% (name)

    song = {}

    for row in g.query(query):
        song["name"]  = name
        song["artist"] = row[0]
        song["date"] = row[1]
        song["cc"] = row[2]
        song["tc"] = row[3]

    return song

def getAllSongsInDate(g, start, end):
    start = "-".join(start) + "T00:00:00"
    end = "-".join(end) + "T00:00:00"

    query = """
    PREFIX mo:  <http://purl.org/ontology/mo/>
    PREFIX rdfs:  <http://www.w3.org/2000/01/rdf-schema#>
    PREFIX time:  <http://www.w3.org/2006/time#>

    SELECT * WHERE {
        ?song a mo:Track .
        ?song rdfs:label ?name.
        ?song time:inXSDDate ?date .

        FILTER (strdt(?date, xsd:dateTime) >= "%s"^^xsd:dateTime) .
        FILTER (strdt(?date, xsd:dateTime) <= "%s"^^xsd:dateTime) .
    }
    order by asc((str(?date)))
    """% (start, end)

    res = list()

    for row in g.query(query):
        res.append(row[2] + " **" +row[0].split("T")[0]+"**")

    return res

def getAllSongs(g):
    query = """
    PREFIX mo:  <http://purl.org/ontology/mo/>
    PREFIX rdfs:  <http://www.w3.org/2000/01/rdf-schema#>
    SELECT * WHERE {
        ?song a mo:Track .
        ?song rdfs:label ?name .
    }
    order by asc(UCASE(str(?name)))
    """
    res = list()
    for row in g.query(query):
        res.append(row[1])

    return res

def getAllArtists(g):
    query = """
    PREFIX mo:  <http://purl.org/ontology/mo/>
    PREFIX rdfs:  <http://www.w3.org/2000/01/rdf-schema#>
    PREFIX owl:  <http://www.w3.org/2002/07/owl#>

    SELECT DISTINCT ?name WHERE {
        ?person a mo:MusicArtist .
        ?person rdfs:label ?name.
    }
    order by asc(UCASE(str(?name)))
    """
    res = list()
    for row in g.query(query):
        res.append(row[0])

    return res

def validateDate(fieldValues):
    error = 0
    try:
        x = [int(f) for f in fieldValues]
    except ValueError:
        error = 1

    if error == 0:
        if x[0] > 2010 or x[0] < 1960:
            gui.msgbox("Year out of range")
            error = 1
        elif x[1] > 12 or x[1] < 1:
            gui.msgbox("Month out of range")
            error = 1
        elif x[2] > 31 or x[2] < 1:
            gui.msgbox("Day out of range")
            error = 1
    else:
        gui.msgbox("All data must be integers")

    return error == 0

def main():
    gui.msgbox("Please, wait until the application starts.\n We are loading the data and doing a lot of cool stuff.")
    print("Loading data...")
    g = Graph()
    g.parse('data/data.rdf')

    reply = ""
    while(reply != None):
        reply = selectSearch()

        if reply == "By song":
            songChoiceBox(g)
        elif reply == "By artist":
            artistChoiceBox(g)
        elif reply == "By date":
            dateInputBox(g)

main()
