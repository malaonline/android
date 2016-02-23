# -*- coding: utf-8 -*-
# Generated by Django 1.9.2 on 2016-02-23 09:22
from __future__ import unicode_literals

from django.db import migrations, models
import django.db.models.deletion


class Migration(migrations.Migration):

    dependencies = [
        ('app', '0080_auto_20160223_1708'),
    ]

    operations = [
        migrations.AlterField(
            model_name='charge',
            name='amount',
            field=models.IntegerField(default=0),
        ),
        migrations.AlterField(
            model_name='charge',
            name='amount_settle',
            field=models.IntegerField(default=0),
        ),
        migrations.AlterField(
            model_name='charge',
            name='created',
            field=models.DateTimeField(blank=True, null=True),
        ),
        migrations.AlterField(
            model_name='charge',
            name='order',
            field=models.ForeignKey(blank=True, null=True, on_delete=django.db.models.deletion.CASCADE, to='app.Order'),
        ),
        migrations.AlterField(
            model_name='charge',
            name='time_expire',
            field=models.DateTimeField(blank=True, null=True),
        ),
        migrations.AlterField(
            model_name='charge',
            name='time_paid',
            field=models.DateTimeField(blank=True, null=True),
        ),
    ]
