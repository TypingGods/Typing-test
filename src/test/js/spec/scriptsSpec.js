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
        beforeEach(function() {
            setTestToStart();
            TypingTest.init();
            TypingTest.testText = "testtest test test test";
            TypingTest.makeCheckPoints();
            jasmine.clock().install();
        });

        it("Should finish the test and stop the counter.", function() {
            $("#start-button").trigger("click");
            typeWithBreaks("testtest test test test", 100);

            var timeLeftBefore = TypingTest.timeLeft;
            jasmine.clock().tick(2000);
            var timeLeftAfter = TypingTest.timeLeft;

            expect(timeLeftBefore).toEqual(timeLeftAfter);
        });

        afterEach(function () {
            jasmine.clock().uninstall();
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

            expect(TypingTest.typingAccuracy()).toEqual("75.00");
        });

    });

    describe("Testing typingSpeedCPM()", function() {
        beforeAll(function() {
            setTestToStart();
        });

        beforeEach(function() {
            TypingTest.init();
            jasmine.clock().install();
        });

        it("Should calculate speed.", function() {
            TypingTest.testText = "testtest";
            TypingTest.makeCheckPoints();

            $("#start-button").trigger("click");
            typeWithBreaks("test", 1000);

            expect(TypingTest.typingSpeedCPM()).toEqual('60.00');
        });

        it("Should calculate speed.", function() {
            TypingTest.testText = "testtest testtest";
            TypingTest.makeCheckPoints();

            $("#start-button").trigger("click");
            typeWithBreaks("testtest testtest", 1000);

            expect(TypingTest.typingSpeedCPM()).toEqual('60.00');
        });

        it("Should calculate speed without taking wrong letters into account.", function() {
            TypingTest.testText = "testtest testtest";
            TypingTest.makeCheckPoints();

            $("#start-button").trigger("click");
            typeWithBreaks("testtestaaaaaaaa", 1000);

            expect(TypingTest.typingSpeedCPM()).toEqual('30.00');
        });

        it("Should calculate speed without taking wrong letters into account.", function() {
            TypingTest.testText = "testtest testtest";
            TypingTest.makeCheckPoints();

            $("#start-button").trigger("click");
            typeWithBreaks("testtest", 200);

            expect(TypingTest.typingSpeedCPM()).toEqual('300.00');
        });

        afterEach(function() {
            setTestToStop();
            jasmine.clock().uninstall();
        });
    });

    describe("Testing makeCheckPoints()", function() {
        beforeAll(function() {
            setTestToStart();
            TypingTest.init();
        });

        it("Should divide testing text to 5 equal parts (except last one).", function() {
            TypingTest.testText = "testtest testtest test test testtesttesta te";
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

        it("Should divide text 'testtest testtest.' to parts of lenghts: 3, 3, 3, 3, 6.", function() {
            TypingTest.testText = "testtest testtest.";
            TypingTest.makeCheckPoints();
            var length = TypingTest.checkPoints.length;

            for(var i = 0; i < length - 2; i++){
                var len = TypingTest.checkPoints[i + 1] - TypingTest.checkPoints[i];
                expect(len).toEqual(3);
            }

            var len = TypingTest.checkPoints[5] - TypingTest.checkPoints[4];

            expect(len).toEqual(6);
        });
    });

    describe("Testing typingPace()", function() {
        beforeEach(function() {
            setTestToStart();
            TypingTest.init();
        });

        afterEach(function() {
            setTestToStop();
        });

        beforeEach(function() {
            jasmine.clock().install();
        });

        it("Should calculate typing pace. Text is typed with changing pace (600cpm and 300cpm). Expect these values in certain checkPoints.", function() {
            TypingTest.testText = "test test test test test ";
            TypingTest.makeCheckPoints();
            $("#start-button").trigger("click");

            typeWithBreaks("test test test", 100);
            typeWithBreaks(" test test ", 200);

            expect(TypingTest.typingPaceMap.get(9)).toEqual("600.00");
            expect(TypingTest.typingPaceMap.get(24)).toEqual("300.00");
        });

        it("Should calculate typing pace. Text is typed with changing pace (200cpm and 400cpm). Expect these values in certain checkPoints.", function() {
            TypingTest.testText = "test example example test. test test example example.";
            TypingTest.makeCheckPoints();
            $("#start-button").trigger("click");

            typeWithBreaks("test example example test. ", 300);
            typeWithBreaks("test test example example.", 150);

            expect(TypingTest.typingPaceMap.get(19)).toEqual("200.00");
            expect(TypingTest.typingPaceMap.get(39)).toEqual("400.00");
        });

        it("Should calculate typing pace. Text is typed with constant pace (600cpm). Expect this values in all checkPoints.", function() {
            TypingTest.testText = "test example example test. test test example example.";
            TypingTest.makeCheckPoints();
            $("#start-button").trigger("click");


            typeWithBreaks("test example example test. test test example example.", 100);

            var speeds = Array.from(TypingTest.typingPaceMap.values());
            for(var i = 0; i < 5; i++)
                expect(speeds[i]).toEqual("600.00");
        });

        afterEach(function() {
            jasmine.clock().uninstall();
        });
    });
});
