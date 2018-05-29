var TypingTest = {
    initialTime: 120,
    timeLeft: 0, //in seconds
    currentSpeed: 0,
    testStarted: false,
    currentLetter: 0,
    correctLetters: 0,
    wrongLetters: 0,
    textLength: 0,
    testText: "",
    textId: 0,
    enteredTextInDOM: undefined,
    timeInDOM: undefined,
    speedInDOM: undefined,
    counter: undefined,
    checkPoints: undefined,
    typingPaceMap: undefined,
    currentCheckPoint: 1,
    lastCheckPoint: 0,//time when last checkPoint speed measuring started
    finalFeedbackSentences: {
        speed : ["Your typing speed is pretty bad, however don't worry about it too much. As long as you focus on accuracy and stable tempo, you are gonna make a great progres. ",
            "Your typing speed is average, but is it enough for you? Of course not! Remember that the most important thing is to stay focused on accuracy and proper tempo of typnig. ",
            "Your typing speed is really awesome. I guess,  you use all fingers while typing. Is there any limit? Of course not! The best typists in the world reach scores around 200 wpm. "],
        accuracy : ["You don't type very accurately, you make a lot of mistakes. Usually it means that you type too fast. Try to slow down and remember, that the first thing to train is accuracy, not speed.  ",
            "You type quite accurately, but there is always something to correct. Remember that if you slow down you will drasticly imporove you accuracy, and it is the crucial thing in your learing process. ",
            "Accuracy is another very important trait of typing. And you know it. You avoided mistakes and it's very cool. Keep it up. "],
        deviation : ["And finally something about typing tempo. Your is very chaotic. In learning process its compulsory to keep stable typing speed. By doing so, after some time, you will notice a great improvement of your speed and accuracy.",
            "And finally some words about typing tempo. Your is medicore. It means you can do better. Try to maintain the same speed during test. By doing so, after some time, you will notice a great improvement of your speed and accuracy.",
            "And finally some words about typing tempo. Your is stable. Seems like you are aware of its importance."],
        graphs : "To analyze your techinque more deeply, checkout two graphs below."
    },
    init: function () {
        var startButton = document.getElementById('start-button');
        startButton.onclick = function () {
            TypingTest.testStarted = true;
            TypingTest.timeLeft = TypingTest.initialTime;
            TypingTest.wrongLetters = 0;
            TypingTest.currentSpeed = 0;
            TypingTest.currentLetter = 0;
            TypingTest.enteredTextInDOM.innerHTML = "";
            window.addEventListener("keydown", TypingTest.readLetter);
            window.addEventListener("keypress", TypingTest.readLetter);
            if(typeof TypingTest.counter !== undefined)
                clearInterval(TypingTest.counter);
            TypingTest.counter = setInterval(function () {
                if (TypingTest.timeLeft > 0) {
                    TypingTest.timeLeft -= 1;
                    TypingTest.timeInDOM.innerHTML = TypingTest.timeLeft;
                    TypingTest.speedInDOM.innerHTML = TypingTest.typingSpeedCPM();
                } else {
                    TypingTest.testStarted = false;
                }
            }, 1000);
            startButton.blur();
            document.getElementById('start-button').disabled = true;
        };
        var resetButton = document.getElementById('reset-button');
        resetButton.onclick = function () {
            window.location.reload(true);
        };
        var testText = document.getElementById('test-text');
        TypingTest.testText = testText.innerHTML;
        var textId = document.getElementById('text-id');
        TypingTest.textId = textId.innerHTML;
        this.timeInDOM = document.getElementById('time');
        this.speedInDOM = document.getElementById('speed');
        this.enteredTextInDOM = document.getElementById('entered-text');
        TypingTest.makeCheckPoints();
        TypingTest.typingPaceMap = new Map();
    },
    finishTest: function () {
        clearInterval(TypingTest.counter);
        console.log(TypingTest.typingPaceMap);
        window.removeEventListener("keydown", TypingTest.readLetter, false);
        window.removeEventListener("keypress", TypingTest.readLetter, false);
        console.log("acc: " + TypingTest.calculatePointsForAccuracy() + " speed: " + TypingTest.calculatePointsForSpeed() + " dev: " + TypingTest.calculatePointsForDeviation());
        var deviation = TypingTest.calculatePointsForDeviation();
        var speed = TypingTest.calculatePointsForSpeed();
        var accuracy = TypingTest.calculatePointsForAccuracy();
        var points = 0.0;
        points += parseInt(deviation);
        points += parseInt(speed);
        points += parseInt(accuracy);
        var grade = TypingTest.getFinalGrade(points);
        console.log(grade);
        TypingTest.displayResults(grade, deviation);
        TypingTest.showFeedback(speed, accuracy, deviation);
        TypingTest.calculateChart();
        TypingTest.showModal();
    },
    readLetter: function (e) {
        if (TypingTest.testStarted) {
            var enteredLetter = e.key;
            //console.log(enteredLetter);
            //console.log(e.keyCode);
            if (e.type === "keypress" && e.keyCode > 31 && e.keyCode < 127) {
                //console.log('keypress');
                //console.log(e.keyCode);
                var newNode = document.createElement("span");
                newNode.style["white-space"] = "pre-wrap";
                newNode.innerHTML = enteredLetter;
                if (enteredLetter === TypingTest.testText.charAt(TypingTest.currentLetter)) {
                    newNode.style["background-color"] = "green";
                    TypingTest.correctLetters += 1;
                } else {
                    newNode.style["background-color"] = "red";
                    TypingTest.wrongLetters += 1;
                }
                //console.log(newNode);
                TypingTest.enteredTextInDOM.appendChild(newNode);
                TypingTest.currentLetter += 1;
                if(TypingTest.correctLetters === TypingTest.testText.length) {
                    TypingTest.finishTest();
                }
            } else if (e.type === 'keydown' && enteredLetter === "Backspace") {
                //console.log('keydown');
                if (TypingTest.enteredTextInDOM.childElementCount > 0) {
                    TypingTest.currentLetter -= 1;
                    if (TypingTest.enteredTextInDOM.lastChild.style['background-color'] === 'green') {
                        TypingTest.correctLetters -=1;
                    }
                    TypingTest.enteredTextInDOM.removeChild(TypingTest.enteredTextInDOM.lastChild);
                }
            }
            TypingTest.typingPace();
        }
    },
    typingSpeedCPM: function () {
        return (TypingTest.correctLetters/((TypingTest.initialTime - TypingTest.timeLeft)/60)).toFixed(2);
    },
    typingAccuracy: function () {
        if (TypingTest.currentLetter > 0) {
            return ((TypingTest.currentLetter - TypingTest.wrongLetters)*100/(TypingTest.currentLetter)).toFixed(2);
        } else {
            return 100;
        }
    },
    makeCheckPoints: function() {
        var textLength = this.testText.length;
        var numOfCheckPoints = 5;

        var checkPoints = new Array();
        var checkPointLength = Math.floor(textLength/numOfCheckPoints);
        var divisionRest = textLength % numOfCheckPoints;

        for(var i = 0; i < numOfCheckPoints; i++){
            checkPoints.push(i * checkPointLength - 1);
        }
        //last checkPoint may be longer
        checkPoints.push((numOfCheckPoints) * checkPointLength - 1 + divisionRest);

        this.checkPoints = checkPoints;
        //console.log(this.checkPoints);
    },
    typingPace: function() {
        //console.log("currentLetter: " + this.currentLetter);
        if(this.currentLetter === this.checkPoints[this.currentCheckPoint]) {
            var temp = this.currentCheckPoint;
            var numOfLetters = this.checkPoints[temp] - this.checkPoints[temp-1];
            var time = this.initialTime - this.timeLeft;
            var currentSpeed = this.currentTypingSpeed(numOfLetters, this.lastCheckPoint, time);

            //console.log("pace: " + currentSpeed);

            this.typingPaceMap.set(this.checkPoints[temp], currentSpeed);
            this.lastCheckPoint = time;
            this.currentCheckPoint++;
        }
    },
    currentTypingSpeed: function(numOfLetters, startTime, endTime){
        return (numOfLetters/(endTime - startTime)*60).toFixed(2);
    },
    calculatePointsForSpeed: function (){
        var speedPoints;
        if (TypingTest.typingSpeedCPM() < 150) {
            speedPoints = 0;
        } else if (TypingTest.typingSpeedCPM() > 500){
            speedPoints = 70;
        } else {
            speedPoints = ((TypingTest.typingSpeedCPM()/5) - 30).toFixed();
        }
        return speedPoints;
    },
    calculatePointsForAccuracy: function () {
        var accuracyPoints;
        if (TypingTest.typingAccuracy() <= 90) {
            accuracyPoints = 0;
        } else {
            accuracyPoints = ((TypingTest.typingAccuracy() - 90).toFixed(0)) * 3;
        }
        return accuracyPoints;
    },
    calculatePointsForDeviation: function () {
        var quotient = 5;
        var deviation = TypingTest.calculateDeviationForSpeed();
        if (deviation > 0 && deviation <= 8 * quotient) {
            return 10;
        } else if ( deviation > 8  * quotient && deviation <= 10  * quotient){
            return 9;
        } else if ( deviation > 10  * quotient && deviation <= 12  * quotient) {
            return 8;
        } else if ( deviation > 12  * quotient && deviation <= 14  * quotient) {
            return 7;
        } else if ( deviation > 14  * quotient && deviation <= 16  * quotient) {
            return 6;
        } else if ( deviation > 16  * quotient && deviation <= 18  * quotient) {
            return 5;
        } else if ( deviation > 18  * quotient && deviation <= 19  * quotient) {
            return 4;
        } else if ( deviation > 19  * quotient && deviation <= 20  * quotient) {
            return 3;
        } else if ( deviation > 20  * quotient && deviation <= 21  * quotient) {
            return 2;
        } else if ( deviation > 21  * quotient && deviation <= 22  * quotient) {
            return 1;
        } else  {
            return 0;
        }
    },
    calculateDeviationForSpeed: function () {
        var speeds = Array.from(TypingTest.typingPaceMap.values()).map(function (elem) { return parseInt(elem)});
        var mean = speeds.reduce(function (acc, curr) { return curr + acc;}, 0)/speeds.length;
        var deviations = speeds.map(function (value) { return (value - mean)*(value - mean);});
        return Math.sqrt(deviations.reduce(function (acc, curr) { return acc + curr;})/deviations.length);
    },
    getTempoDescription: function (points) {
        var tempos = ["chaotic","unstable","mediocre","stable","very stable"];
        if (points >= 0 && points <= 2) {
            return tempos[0];
        } else if (points > 2 && points <= 4) {
            return tempos[1];
        } else if (points > 4 && points <= 6) {
            return tempos[2];
        } else if (points > 6 && points <= 8) {
            return tempos[3];
        } else if (points > 8 && points <= 10) {
            return tempos[4];
        }
    },
    getFinalGrade: function (points) {
        points = parseInt(points);
        if (points >= 100){
            return "S";
        } else if (points >= 95){
            return "A+";
        } else if (points >= 90){
            return "A";
        } else if (points >= 85){
            return "A-";
        } else if (points >= 80){
            return "B+";
        } else if (points >= 75){
            return "B";
        } else if (points >= 70){
            return "B-";
        } else if (points >= 65){
            return "C+";
        } else if (points >= 60){
            return "C";
        } else if (points >= 55){
            return "C-";
        } else if (points >= 50){
            return "D+";
        } else if (points >= 45){
            return "D";
        } else if (points >= 40){
            return "D-";
        } else if (points >= 35) {
            return "F";
        } else {
            return "err";
        }
    },
    showFeedback: function (speed, accuracy, deviation) {
        var sentence = "";
        if (speed >= 0 && speed <= 20) {
            sentence += TypingTest.finalFeedbackSentences.speed[0];
        } else if (speed > 20 && speed <= 50) {
            sentence += TypingTest.finalFeedbackSentences.speed[1];
        } else {
            sentence += TypingTest.finalFeedbackSentences.speed[2];
        }
        if (accuracy >= 0 && accuracy <= 10) {
            sentence += TypingTest.finalFeedbackSentences.accuracy[0];
        } else if (accuracy > 10 && accuracy <= 20) {
            sentence += TypingTest.finalFeedbackSentences.accuracy[1];
        } else {
            sentence += TypingTest.finalFeedbackSentences.accuracy[2];
        }
        if (deviation >= 0 && deviation <= 4) {
            sentence += TypingTest.finalFeedbackSentences.deviation[0];
        } else if (deviation > 4 && deviation <= 6) {
            sentence += TypingTest.finalFeedbackSentences.deviation[1];
        } else {
            sentence += TypingTest.finalFeedbackSentences.deviation[2];
        }
        sentence += TypingTest.finalFeedbackSentences.graphs;
        var feedback = document.getElementById("feedback");
        feedback.innerHTML = sentence;
        feedback.style.display = "block";
    },
    showModal: function () {
        var modal = document.getElementById("nickname-modal");
        modal.style.display = "block";
    },
    displayResults: function (grade, deviationPoints) {
        document.getElementById("scores").style.display = "block";
        document.getElementById("score-speed").innerHTML = TypingTest.typingSpeedCPM() + " CPM";
        document.getElementById("score-accuracy").innerHTML = TypingTest.typingAccuracy() + "%";
        document.getElementById("score-mistakes").innerHTML = TypingTest.wrongLetters.toString();
        document.getElementById("score-tempo").innerHTML = TypingTest.getTempoDescription(deviationPoints);
        document.getElementById("score-grade").innerHTML = grade;
    },
    calculateChart: function () {
        var speeds = Array.from(TypingTest.typingPaceMap.values());
        console.log(speeds);

        var barChart = new CanvasJS.Chart("bar-chart", {
            animationEnabled: true,
            animationDuration: 2000,
            title:{
                text: "Speed throughout the test"
            },
            axisY: {
                includeZero: false,
                stripLines: [{
                    lineColor: "crimson",
                    lineThickness: 2,
                    color: "red",
                    labelFontColor: "red",
                    value: TypingTest.typingSpeedCPM(),
                    label: "Average"
                }]
            },
            data: [
                {
                    fillOpacity: .5,
                    type: "column",
                    dataPoints: [
                        { label: "part 1",  y: parseInt(speeds[0])},
                        { label: "part 2", y: parseInt(speeds[1])},
                        { label: "part 3", y: parseInt(speeds[2])},
                        { label: "part 4",  y: parseInt(speeds[3])},
                        { label: "part 5",  y: parseInt(speeds[4])}
                    ]
                }
            ]

        });
        document.getElementById('bar-chart').style.height = "370px";
        barChart.render();

        var linearChart = new CanvasJS.Chart("linear-chart", {
            animationEnabled: true,
            animationDuration: 2000,
            title:{
                text: "Average speed throughout test"
            },
            axisY: {
                includeZero: false
            },
            data: [{
                type: "spline",
                lineColor: "#82a6fe",
                markerColor: "red",
                lineThickness: 2,
                dataPoints: [
                    {label: "check 1", y: parseInt(speeds[0])},
                    {label: "check 2", y: parseInt(speeds[1])},
                    {label: "check 3", y: parseInt(speeds[2])},
                    {label: "check 4", y: parseInt(speeds[3])},
                    {label: "check 5", y: parseInt(speeds[4])}
                ]
            }]
        });
        document.getElementById('linear-chart').style.height = "370px";
        linearChart.render();


    }
};
TypingTest.init();

function toggleVisibility(id){
    var element = document.getElementById(id);
    element.style.display = element.style.display === "block" ? "none" : "block";
}

function sendRequest(){
    var nickname = document.getElementById("nickname-input").value;
    toggleVisibility("nickname-modal");

    var url = document.URL + "/database";
    $.post(url,
        {
            userName : nickname,
            score: "10",
            textId: TypingTest.textId
        },
        function(data){
            $("html").html(data);
        });

}