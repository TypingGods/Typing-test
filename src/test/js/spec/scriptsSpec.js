describe("Testing scripts.js", function() {
    afterEach(function() {
        setTestToStop();
    });

    describe("Testing readLetter(e)", function() {
        beforeEach(function() {
            removeChildren("entered-text");
            TypingTest.init();
            TypingTest.testText = "test";
            TypingTest.testStarted = true;
        });

        it("Should add 1 span with green background-color.", function() {
            var numOfSpansBefore = document.getElementsByTagName("span").length;

            var e = jQuery.Event("keypress", {keyCode: 74, key: 't'});
            TypingTest.readLetter(e);
            var paragraphForTestText = document.getElementById("entered-text");
            var backColor = paragraphForTestText.lastChild.style.backgroundColor;

            var numOfSpansAfter = document.getElementsByTagName("span").length;

            expect(numOfSpansAfter).toEqual(numOfSpansBefore + 1);
            expect(backColor).toEqual("green");
        });

        it("Should add 1 span and remove it.", function() {
            var numOfSpansBefore = document.getElementsByTagName("span").length;

            var e = jQuery.Event("keypress", {keyCode: 74, key: "t"});
            TypingTest.readLetter(e);
            var e = jQuery.Event("keydown", {keyCode: 32, key: "Backspace"});
            TypingTest.readLetter(e);

            var numOfSpansAfter = document.getElementsByTagName("span").length;

            expect(numOfSpansAfter).toEqual(numOfSpansBefore);
        });

        afterAll(function() {
            removeChildren("entered-text");
        });
    });

    describe("Testing finishTest()", function() {
        beforeAll(function() {
            setTestToStart();
            TypingTest.init();
            TypingTest.testText = "test";
            TypingTest.testStarted = true;
        });

        it("Should finish the test and stop the counter (set it to undefined).", function() {
            var e = jQuery.Event("keypress", {keyCode: 74, key: 't'});
            TypingTest.readLetter(e);
            e = jQuery.Event("keypress", {keyCode: 65, key: 'e'});
            TypingTest.readLetter(e);
            e = jQuery.Event("keypress", {keyCode: 73, key: 's'});
            TypingTest.readLetter(e);
            e = jQuery.Event("keypress", {keyCode: 74, key: 't'});
            TypingTest.readLetter(e);

            expect(TypingTest.counter).toEqual(undefined);
        });

    });

    describe("Testing typingAccuracy()", function() {
        beforeAll(function() {
            setTestToStart();
            TypingTest.init();
            TypingTest.testText = "test";
            TypingTest.testStarted = true;
        });

        it("Should calculate accuracy.", function() {
            var e = jQuery.Event("keypress", {keyCode: 74, key: 't'});
            TypingTest.readLetter(e);
            e = jQuery.Event("keypress", {keyCode: 65, key: 'e'});
            TypingTest.readLetter(e);
            e = jQuery.Event("keypress", {keyCode: 73, key: 's'});
            TypingTest.readLetter(e);
            e = jQuery.Event("keypress", {keyCode: 74, key: 'a'});
            TypingTest.readLetter(e);

            //console.log(TypingTest.currentLetter);
            //console.log(TypingTest.wrongLetters);

            expect(TypingTest.typingAccuracy()).toEqual(75);
        });

    });

    describe("Testing typingSpeed()", function() {
        beforeAll(function() {
            setTestToStart();
            TypingTest.init();
            TypingTest.testText = "testtest";
        });

        beforeEach(function() {
            var timerCallBack = jasmine.createSpy("timerCallBack");
            jasmine.clock().install();
        });

        it("Should calculate speed.", function() {
            $("#start-button").trigger("click");

            jasmine.clock().tick(1000);
            var e = jQuery.Event("keypress", {keyCode: 74, key: 't'});
            TypingTest.readLetter(e);
            jasmine.clock().tick(1000);
            e = jQuery.Event("keypress", {keyCode: 65, key: 'e'});
            TypingTest.readLetter(e);
            jasmine.clock().tick(1000);
            e = jQuery.Event("keypress", {keyCode: 73, key: 's'});
            TypingTest.readLetter(e);
            jasmine.clock().tick(1000);
            e = jQuery.Event("keypress", {keyCode: 74, key: 't'});
            TypingTest.readLetter(e);

            expect(TypingTest.typingSpeedCPM()).toEqual('60.00');
        });

        afterEach(function() {
            jasmine.clock().uninstall();
        });
    });

    describe("Testing makeCheckPoints()", function() {
        beforeAll(function() {
            setTestToStart();
            TypingTest.init();
            TypingTest.testText = "testtest testtest test test testtesttesta te";
        });

        it("Should divide testing text to 5 equal parts (except last one).", function() {
            TypingTest.makeCheckPoints();
            var length = TypingTest.checkPoints.length;
            for(var i = 0; i < length - 3; i++){
                var len1 = TypingTest.checkPoints[i + 1] - TypingTest.checkPoints[i];
                var len2 = TypingTest.checkPoints[i + 2] - TypingTest.checkPoints[i + 1];
                expect(len1).toEqual(len2);
            }

            var len1 = TypingTest.checkPoints[length - 2] - TypingTest.checkPoints[length - 3];
            var len2 = TypingTest.checkPoints[length - 1] - TypingTest.checkPoints[length - 2];

            expect(len2).toBeGreaterThanOrEqual(len1);
        });
    });

    describe("Testing typingPace()", function() {
        beforeAll(function() {
            setTestToStart();
            TypingTest.init();
            TypingTest.testText = "test test test test test ";
            TypingTest.makeCheckPoints();
        });

        beforeEach(function() {
            var timerCallBack = jasmine.createSpy("timerCallBack");
            jasmine.clock().install();
        });

        it("Should calculate typing pace. Text is typed with changing pace (600cpm and 300cpm). Expect these values in certain checkPoints.", function() {
            $("#start-button").trigger("click");

            typeWithBreaks("test test test", 100);
            typeWithBreaks(" test test ", 200);

            expect(TypingTest.typingPaceMap.get(9)).toEqual("600.00");
            expect(TypingTest.typingPaceMap.get(24)).toEqual("300.00");
        });

        afterEach(function() {
            jasmine.clock().uninstall();
        });
    });
});
