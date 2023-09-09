$(document).ready(function() {
  var currentDate = new Date();
  //alert(currentDate);
  function generateCalendar(d) {
    function monthDays(month, year) {
      var result = [];
      var days = new Date(year, month, 0).getDate();
      //console.log(days);
      for (var i = 1; i <= days; i++) {
        result.push(i);
      }
      return result;
    }
    Date.prototype.monthDays = function() {
      var d = new Date(this.getFullYear(), this.getMonth() + 1, 0);
      return d.getDate();
    };


    var details = {
      // totalDays: monthDays(d.getMonth(), d.getFullYear()),
      totalDays: d.monthDays(),
      weekDays: ['Sun', 'Mon', 'Tue', 'Wed', 'Thur', 'Fri', 'Sat'],
      months: ['January', 'February', 'March', 'April', 'May', 'June', 'July', 'August', 'September', 'October', 'November', 'December'],
    };
    var response = {"datesMap":{}};
    $.ajax({
        url:'/getMonthDays',
        method:'post',
        data: {"month": d.getMonth() + 1,"year":d.getFullYear() },
        success: function (responses){
        //console.log(responses);
        response = responses;
        //console.log(response);
        }
           }).done(function(){
           var start = new Date(d.getFullYear(), d.getMonth()).getDay();

               var cal = [];
               var day = 1;
               for (var i = 0; i <= 6; i++) {
                 cal.push(['<tr>']);
                 for (var j = 0; j < 7; j++) {
                   if (i === 0) {
                     cal[i].push('<th style="width:50px;background-color:lightgrey;">' + details.weekDays[j] + '</th>');
                   } else if (day > details.totalDays) {
                     cal[i].push('<td>&nbsp;</td>');
                   } else {
                     if (i === 1 && j < start) {
                       cal[i].push('<td>&nbsp;</td>');
                     } else {
                     var month = d.getMonth()+1;
                     //console.log(month);
                      var datte = d.getFullYear()+"-" +month+"-" +(day).toString()
                      //alert(datte);
                     if (Object.keys(response['datesMap']).includes((day).toString()) ){
                     //console.log(response['datesMap'][(day).toString()]);
                     cal[i].push('<td class="day" style="background-color:'+ response['datesMap'][(day).toString()][1] +';" onClick="getSlots(\''+ datte + '\')" >' + day++ + '</td>');

                     }else{
                       cal[i].push('<td class="day" style="background-color:'+ response['defaultColor']+'"  onClick="getSlots(\''+ datte + '\')" >'+ day++ + '</td>');
                       }
                     }
           //cal[i].push('<td class="day" style="background-color:'+ response.datesMap[day++][1] +';" >' + day++ + '</td>');

                   }
                 }
                 cal[i].push('</tr>');
               }
               //console.log(cal);
               cal = cal.reduce(function(a, b) {
                 return a.concat(b);
               }, []).join('');
               $('.caltable').append(cal);
               $('#month').text(details.months[d.getMonth()]);
               $('#year').text(d.getFullYear());
               $('td.day').mouseover(function() {
                 $(this).addClass('hover');
               }).mouseout(function() {
                 $(this).removeClass('hover');
               });
           })


  }

  $('#left').click(function() {
    $('.caltable').text('');
    if (currentDate.getMonth() === 0) {
      currentDate = new Date(currentDate.getFullYear() - 1, 11);
      generateCalendar(currentDate);
    } else {
      currentDate = new Date(currentDate.getFullYear(), currentDate.getMonth() - 1)
      generateCalendar(currentDate);
     // alert(currentDate);
    }
  });
  $('#right').click(function() {
    $('.caltable').html('<tr></tr>');
    if (currentDate.getMonth() === 11) {
      currentDate = new Date(currentDate.getFullYear() + 1, 0);
      generateCalendar(currentDate);
      //alert(currentDate);
    } else {
      currentDate = new Date(currentDate.getFullYear(), currentDate.getMonth() + 1)
      generateCalendar(currentDate);
     // alert(currentDate);
    }
  });
  generateCalendar(currentDate);
});

function getSlots(d){
//console.log("fhsdaf");
    //console.log(d);
    $(".requests").css("display", "none");

    $.ajax({
            url:'/getSlots',
            method:'get',
            data: {"date":d},
            success: function (response){
             $('.courts').empty();
            for(slotid in response.slots){
                var slot = response.slots[slotid];
            $('.table-'+slot['courtCode']).empty();
            $('.'+slot['courtCode']).append('<p onclick="showRequests(\''+ slot['id'] +'\',\''+ slot['courtCode'] +'\')" class="card-block" style="background-color: '+slot['statusColor']+';" >'+ slot['startHour']+"-"+slot['endHour'] +'-'+ slot['aprSlots']+' out of '+slot['totalAvb'] +'</p>');
            }
            //


            for(requestId in  response.bookSlotMap){
            var request = response.bookSlotMap[requestId];
                //console.log(request
//                tableTdHtml = '<tr style="display:none;" class="requests request-'+ request['slotId'] + '-'+request['gameMode']  +' '+request['gameMode']  +'"><td scope="col"  >'+ request['user']['userName'] +'</td><td scope="col"  >'+ request['user']['userType'] +'</td><td scope="col"  >'+ request['refNo'] +'</td><td scope="col">'+ request['gameMode'] +'</td><td scope="col"> '+ request['confirmStatus'] +'</td> <td scope="col">  '+ request['remarksByUser'] +'</td><td scope="col" >'+ request['remarksByAdmin'] +'  </td> '
                tableTdHtml = '<tr style="display:none;" class="requests request-'+ request['slotId'] + '-'+request['gameMode'] +'"><td scope="col"  >'+ request['user']['userName'] +'</td><td scope="col"  >'+ request['user']['userType'] +'</td><td scope="col"  >'+ request['refNo']+'</td><td scope="col"  >'+ request['gameMode'] +'</td><td scope="col"> '+ request['confirmStatus'] +'</td> <td scope="col">  '+ request['remarksByUser'] +'</td><td scope="col" ><input type="text" class="form-control" id="remarksAdm-'+ request['id']+ '" > </td>  <td scope="col"> <p onclick="approveSlot(\''+ request['id'] +'\')" class="btn btn-success">Approve</p></td><td> <p onclick="rejectSlot(\''+ request['id'] +'\')" class="btn btn-danger">Reject</p></td><td> <p onclick="notifySlot(\''+ request['id'] +'\')" class="btn btn-primary">Notify</p></td></tr>'
                if(request['confirmStatus'] == 'pending'){
                tableTdHtml = '<tr style="display:none;" class="requests request-'+ request['slotId'] + '-'+request['gameMode'] +'"><td scope="col"  >'+ request['user']['userName'] +'</td><td scope="col"  >'+ request['user']['userType'] +'</td><td scope="col"  >'+ request['refNo']+'</td><td scope="col"  >'+ request['gameMode'] +'</td><td scope="col"> '+ request['confirmStatus'] +'</td> <td scope="col">  '+ request['remarksByUser'] +'</td><td scope="col" ><input type="text" class="form-control" id="remarksAdm-'+ request['id']+ '" > </td>  <td scope="col"> <p onclick="approveSlot(\''+ request['id'] +'\')" class="btn btn-success">Approve</p></td><td> <p onclick="rejectSlot(\''+ request['id'] +'\')" class="btn btn-danger">Reject</p></td><td> <p onclick="notifySlot(\''+ request['id'] +'\')" class="btn btn-primary">Notify</p></td></tr>'

                }else if(request['confirmStatus'] == 'accepted'){
                 tableTdHtml = '<tr style="display:none;" class="requests request-'+ request['slotId'] + '-'+request['gameMode'] +'"><td scope="col"  >'+ request['user']['userName'] +'</td><td scope="col"  >'+ request['user']['userType'] +'</td><td scope="col"  >'+ request['refNo']+'</td><td scope="col"  >'+ request['gameMode'] +'</td><td scope="col"> '+ request['confirmStatus'] +'</td> <td scope="col">  '+ request['remarksByUser'] +'</td><td scope="col" ><input type="text" class="form-control" id="remarksAdm-'+ request['id']+ '" > </td>  <td scope="col"> <p class="btn btn-secondary" disabled>Approve</p></td><td> <p onclick="rejectSlot(\''+ request['id'] +'\')" class="btn btn-danger">Reject</p></td><td> <p onclick="notifySlot(\''+ request['id'] +'\')" class="btn btn-primary">Notify</p></td></tr>'

                }else if(request['confirmStatus'] == 'rejected'){
                tableTdHtml = '<tr style="display:none;" class="requests request-'+ request['slotId'] + '-'+request['gameMode'] +'"><td scope="col"  >'+ request['user']['userName'] +'</td><td scope="col"  >'+ request['user']['userType'] +'</td><td scope="col"  >'+ request['refNo']+'</td><td scope="col"  >'+ request['gameMode'] +'</td><td scope="col"> '+ request['confirmStatus'] +'</td> <td scope="col">  '+ request['remarksByUser'] +'</td><td scope="col" ><input type="text" class="form-control" id="remarksAdm-'+ request['id']+ '" > </td>  <td scope="col"> <p onclick="approveSlot(\''+ request['id'] +'\')" class="btn btn-success">Approve</p></td><td> <p class="btn btn-secondary" disabled >Reject</p></td><td> <p onclick="notifySlot(\''+ request['id'] +'\')" class="btn btn-primary">Notify</p></td></tr>'

                }else if(request['confirmStatus'] == 'notified'){
                tableTdHtml = '<tr style="display:none;" class="requests request-'+ request['slotId'] + '-'+request['gameMode'] +'"><td scope="col"  >'+ request['user']['userName'] +'</td><td scope="col"  >'+ request['user']['userType'] +'</td><td scope="col"  >'+ request['refNo']+'</td><td scope="col"  >'+ request['gameMode'] +'</td><td scope="col"> '+ request['confirmStatus'] +'</td> <td scope="col">  '+ request['remarksByUser'] +'</td><td scope="col" ><input type="text" class="form-control" id="remarksAdm-'+ request['id']+ '" > </td>  <td scope="col"> <p onclick="approveSlot(\''+ request['id'] +'\')" class="btn btn-success">Approve</p></td><td> <p onclick="rejectSlot(\''+ request['id'] +'\')" class="btn btn-danger">Reject</p></td><td> <p disabled  class="btn btn-secondary">Notify</p></td></tr>'
                }
                $('.table-'+request['courtCode']).append(tableTdHtml);
            }
            }});
}

