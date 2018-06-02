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

    init: function () {
        var startButton = document.getElementById('start-button');
        var refreshDOM = 0;
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
                refreshDOM++;
                if (TypingTest.timeLeft > 0) {
                    TypingTest.timeLeft -= 0.1;

                    if(refreshDOM % 10 == 0) {
                        TypingTest.timeInDOM.innerHTML = TypingTest.timeLeft.toFixed(0);
                        TypingTest.speedInDOM.innerHTML = TypingTest.typingSpeedCPM();
                    }
                } else {
                    TypingTest.testStarted = false;
                }
            }, 100);
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
        console.log(TypingTest.getFinalGrade(points));
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
            return (TypingTest.currentLetter - TypingTest.wrongLetters)*100/(TypingTest.currentLetter);
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
    showModal: function () {
        var modal = document.getElementById("nickname-modal");
        modal.style.display = "block";
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